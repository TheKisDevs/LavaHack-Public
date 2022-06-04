package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = EntityPlayer.class, priority = Integer.MAX_VALUE)
public class MixinEntityPlayer extends MixinEntityLivingBase {
    public MixinEntityPlayer(World worldIn) {super(worldIn);}

    @Shadow protected void doWaterSplashEffect() {}
    @Shadow public String getName() {return null;}

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        if(Minecraft.getMinecraft().player.getName().equals(getName())) {
            EventPlayerJump event = new EventPlayerJump();
            Kisman.EVENT_BUS.post(event);
            if(event.isCancelled()) ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        EventPlayerTravel event = new EventPlayerTravel(strafe, vertical, forward);
        Kisman.EVENT_BUS.post(event);

        if(event.isCancelled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            ci.cancel();
        }
    }

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    private void applyEntityCollision(Entity entity, CallbackInfo ci) {
        EventPlayerApplyCollision event = new EventPlayerApplyCollision(entity);
        Kisman.EVENT_BUS.post(event);

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "isPushedByWater()Z", at = @At("HEAD"), cancellable = true)
    private void isPushedByWater(CallbackInfoReturnable<Boolean> cir) {
        EventPlayerPushedByWater event = new EventPlayerPushedByWater();
        Kisman.EVENT_BUS.post(event);

        if (event.isCancelled()) cir.setReturnValue(false);
    }
}
