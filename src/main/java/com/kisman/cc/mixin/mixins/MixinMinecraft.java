 package com.kisman.cc.mixin.mixins;

 import com.kisman.cc.Kisman;
 import com.kisman.cc.mixin.mixins.accessor.*;
 import com.kisman.cc.module.exploit.MultiTask;
 import com.kisman.cc.viaforge.ViaForge;
 import net.minecraft.client.entity.EntityPlayerSP;
 import net.minecraft.client.main.GameConfiguration;
 import net.minecraft.client.multiplayer.PlayerControllerMP;
 import net.minecraft.client.settings.GameSettings;
 import org.spongepowered.asm.mixin.Mixin;

 import net.minecraft.client.Minecraft;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.*;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 @Mixin(value = Minecraft.class, priority = 10000)
 public class MixinMinecraft {
  @Shadow public GameSettings gameSettings;
  @Shadow public EntityPlayerSP player;
  @Shadow public PlayerControllerMP playerController;
  @Shadow private void clickMouse() {};

  private boolean mt_handActive = false;
  private boolean mt_isHittingBlock = false;

  @Inject( method = "processKeyBinds", at = @At( value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z", shift = At.Shift.BEFORE, ordinal = 2 ) )
  public void mt_processKeyBinds( CallbackInfo info ) {
   if(MultiTask.instance.isToggled()) {
    while(gameSettings.keyBindAttack.isPressed())
     clickMouse();
   }
  }

  @Inject( method = "rightClickMouse", at = @At( "HEAD" ) )
  public void mt_rightClickMouse( CallbackInfo info ) {
   if(MultiTask.instance.isToggled()) {
    mt_isHittingBlock = playerController.getIsHittingBlock();
    ((AccessorPlayerControllerMP) playerController).mm_setIsHittingBlock(false);
   }
  }

  @Inject(method = "rightClickMouse", at = @At("RETURN"))
  public void mt_rightClickMousePost(CallbackInfo ci) {
   if (MultiTask.instance.isToggled() && !playerController.getIsHittingBlock()) ((AccessorPlayerControllerMP) playerController).mm_setIsHittingBlock(mt_isHittingBlock);
  }

  @Inject(method = "sendClickBlockToController", at = @At("HEAD"))
  public void mt_sendClickBlockToControllerPre(boolean leftClick, CallbackInfo ci) {
   if (MultiTask.instance.isToggled()) {
    mt_handActive = player.isHandActive();
    ((IEntityPlayerSP) player).mm_setHandActive(false);
   }
  }

  @Inject(method = "sendClickBlockToController", at = @At("RETURN"))
  public void mt_sendClickBlockToControllerPost(boolean leftClick, CallbackInfo ci) {
   if (MultiTask.instance.isToggled() && !player.isHandActive()) ((IEntityPlayerSP) player).mm_setHandActive(mt_handActive);
  }

  @Inject(method = "<init>", at = @At("RETURN"))
  public void injectConstructor(GameConfiguration p_i45547_1_, CallbackInfo ci) {
   try {ViaForge.getInstance().start();} catch (Exception e) {Kisman.LOGGER.error("[ViaForge] ViaForge did not loaded! If you need it, restart the client");}}
 }
