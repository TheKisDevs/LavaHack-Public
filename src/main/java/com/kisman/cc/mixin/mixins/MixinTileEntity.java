package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.misc.Optimizer;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntity.class)
public class MixinTileEntity {
    @Inject(method = "getMaxRenderDistanceSquared", at = @At("HEAD"), cancellable = true)
    public void doGetMaxRenderDistanceSquared(CallbackInfoReturnable<Double> cir) {
        if(Optimizer.instance != null && Optimizer.instance.isToggled()) cir.setReturnValue(Optimizer.instance.tileEntityRenderRange.getValDouble());
    }
}
