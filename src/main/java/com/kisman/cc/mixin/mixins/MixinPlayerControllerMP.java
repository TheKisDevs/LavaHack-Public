package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.player.Reach;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerControllerMP.class, priority = 10000)
public class MixinPlayerControllerMP {
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> callback) {
        if (Reach.instance.isToggled()) {
            callback.setReturnValue(Reach.instance.distance.getValFloat());
            callback.cancel();
        }
    }

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    private void clickBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        EventDamageBlock event = new EventDamageBlock(posBlock, directionFacing);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Dynamic
    @Inject(
        method = "onPlayerDestroyBlock",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/block/Block.removedByPlayer" +
                    "(Lnet/minecraft/block/state/IBlockState;" +
                     "Lnet/minecraft/world/World;" +
                     "Lnet/minecraft/util/math/BlockPos;" +
                     "Lnet/minecraft/entity/player/EntityPlayer;Z)Z",
            remap = false),
        cancellable = true)
    private void onPlayerDestroyBlockHook(BlockPos pos,
                                          CallbackInfoReturnable<Boolean> info)
    {
        DestroyBlockEvent event = new DestroyBlockEvent(Event.Era.PRE, pos);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            info.setReturnValue(false);
        }
    }
}
