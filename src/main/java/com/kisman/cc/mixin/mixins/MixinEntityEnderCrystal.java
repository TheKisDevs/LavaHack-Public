package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventCrystalAttack;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityEnderCrystal.class, priority = 1000)
public class MixinEntityEnderCrystal {
    @Inject(method = "attackEntityFrom", at = @At("RETURN"), cancellable = true)
    public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getTrueSource() != null) {
            EventCrystalAttack event = new EventCrystalAttack(source.getTrueSource().entityId);
            Kisman.EVENT_BUS.post(event);
            if (event.isCancelled()) cir.cancel();
        }
    }
}