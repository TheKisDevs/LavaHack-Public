package com.kisman.cc.mixin.mixins;

import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventSetOpaqueCube;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph{
    @Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
    public void setOpaqueCube(BlockPos pos, CallbackInfo ci){
        EventSetOpaqueCube event = new EventSetOpaqueCube();
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}