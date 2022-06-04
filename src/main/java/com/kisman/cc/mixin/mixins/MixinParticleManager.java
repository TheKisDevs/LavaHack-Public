package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(method = "emitParticleAtEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumParticleTypes;)V", at = @At("HEAD"), cancellable = true)
    private void first(Entity entityIn, EnumParticleTypes particleTypes, CallbackInfo ci) {
        if(canCancel(particleTypes)) ci.cancel();
    }

    @Inject(method = "emitParticleAtEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/EnumParticleTypes;I)V", at = @At("HEAD"), cancellable = true)
    private void second(Entity p_191271_1_, EnumParticleTypes p_191271_2_, int p_191271_3_, CallbackInfo ci) {
        if(canCancel(p_191271_2_)) ci.cancel();
    }

    private boolean canCancel(EnumParticleTypes particleTypes) {
        if(NoRender.instance.particle.getValString().equalsIgnoreCase("None") || !NoRender.instance.isToggled()) return false;
        return !NoRender.instance.particle.getValString().equalsIgnoreCase(NoRender.ParticleMode.AllButIgnorePops.name()) || !particleTypes.equals(EnumParticleTypes.TOTEM);
    }
}
