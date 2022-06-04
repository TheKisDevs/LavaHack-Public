package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.combat.KillAura;
import com.kisman.cc.module.render.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, priority = 10000)
public class MixinItemRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    @Shadow private void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_) {}

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER))
    private void transformSideFirstPersonInvokePushMatrix(AbstractClientPlayer player, float partialTicks, float pitch, EnumHand hand, float swingProgress, ItemStack stack, float equippedProgress, CallbackInfo ci) {
        if(ViewModel.instance.hands.getValBoolean()) ViewModel.instance.hand(hand.equals(EnumHand.MAIN_HAND) ? player.getPrimaryHand() : player.getPrimaryHand().opposite());
    }

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
        if(ViewModel.instance.isToggled() || SwingAnimation.instance.isToggled()) {
            float rotateMainX = 0;
            float rotateMainY = 0;
            float rotateMainZ = 0;

            boolean isEating = PlayerUtil.IsEating();
            boolean isSwing = mc.player.swingProgress > 0 && SwingAnimation.instance.isToggled() && SwingAnimation.instance.mode.getValString().equalsIgnoreCase("Strong");
            boolean isSwingMain = (SwingAnimation.instance.ifKillAura.getValBoolean() && KillAura.instance.isToggled() && KillAura.instance.target != null || isSwing) && hand == EnumHandSide.RIGHT && (!SwingAnimation.instance.ignoreEating.getValBoolean() || !isEating);

            if (isSwingMain) {
                switch (SwingAnimation.instance.strongMode.getValString()) {
                    case "Blockhit1": {
                        rotateMainX = 72;
                        rotateMainY = 180;
                        rotateMainZ = 240;
                        break;
                    }
                    case "Blockhit2": {
                        rotateMainX = 344;
                        rotateMainY = 225;
                        rotateMainZ = 0;
                        break;
                    }
                    case "Knife": {
                        rotateMainX = 43;
                        rotateMainY = 130;
                        rotateMainZ = 230;
                    }
                }
            } else if (mc.player.swingProgress == 0) {
                rotateMainX = 0;
                rotateMainY = 0;
                rotateMainZ = 0;
            }

            if (Kisman.instance.moduleManager.getModule("ViewModel").isToggled() && hand == EnumHandSide.RIGHT) {
                if(isEating && !ViewModel.instance.customEating.getValBoolean()) drawDefaultPos(hand, y);
                else {
                    if(ViewModel.instance.translate.getValBoolean()) GlStateManager.translate(getSet("RightX").getValDouble(), getSet("RightY").getValDouble(), getSet("RightZ").getValDouble());
                    else this.transformSideFirstPerson(hand, y);
                    GlStateManager.rotate(isSwingMain ? rotateMainX : (!ViewModel.instance.autoRotateRigthX.getValBoolean() ? ((float) (getSet("RotateRightX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
                    GlStateManager.rotate(isSwingMain ? rotateMainY : (!ViewModel.instance.autoRotateRigthY.getValBoolean() ? ((float) (getSet("RotateRightY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
                    GlStateManager.rotate(isSwingMain ? rotateMainZ : (!ViewModel.instance.autoRotateRigthZ.getValBoolean() ? ((float) (getSet("RotateRightZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
                }
                GlStateManager.scale(ViewModel.instance.scaleRightX.getValDouble(), ViewModel.instance.scaleRightY.getValDouble(), ViewModel.instance.scaleRightZ.getValDouble());
            }

            if (Kisman.instance.moduleManager.getModule("ViewModel").isToggled() && hand == EnumHandSide.LEFT) {
                if(PlayerUtil.isEatingOffhand() && !ViewModel.instance.customEating.getValBoolean()) drawDefaultPos(hand, y);
                else {
                    if(ViewModel.instance.translate.getValBoolean()) GlStateManager.translate(getSet("LeftX").getValDouble(), getSet("LeftY").getValDouble(), getSet("LeftZ").getValDouble());
                    else this.transformSideFirstPerson(hand, y);
                    GlStateManager.rotate((!ViewModel.instance.autoRotateLeftX.getValBoolean() ? ((float) (getSet("RotateLeftX").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 1, 0, 0);
                    GlStateManager.rotate((!ViewModel.instance.autoRotateLeftY.getValBoolean() ? ((float) (getSet("RotateLeftY").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 1, 0);
                    GlStateManager.rotate((!ViewModel.instance.autoRotateLeftZ.getValBoolean() ? ((float) (getSet("RotateLeftZ").getValDouble())) : (float) (System.currentTimeMillis() % 22600L) / 5.0f), 0, 0, 1);
                }
                GlStateManager.scale(ViewModel.instance.scaleLeftX.getValDouble(), ViewModel.instance.scaleLeftY.getValDouble(), ViewModel.instance.scaleLeftZ.getValDouble());
            }
        } else this.transformSideFirstPerson(hand, y);
    }

    private void drawDefaultPos(EnumHandSide hand, float y) {
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate((float)i * 0.56F, -0.52F + y * -0.6F, -0.72F);
    }

    private Setting getSet(String name) {
        return Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule("ViewModel"), name);
    }
}