package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerControllerMP.class)
public interface AccessorPlayerControllerMP {
    @Accessor("isHittingBlock")
    void mm_setIsHittingBlock(boolean value);
}
