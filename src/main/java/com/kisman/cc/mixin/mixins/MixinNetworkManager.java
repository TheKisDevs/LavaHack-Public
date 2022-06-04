package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class, priority = 10000)
public class MixinNetworkManager {
    @Shadow public boolean isChannelOpen() {return false;}

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> p_Packet, CallbackInfo callbackInfo) {
        NetworkPacketEvent event = new NetworkPacketEvent(p_Packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void preSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void preChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("TAIL"), cancellable = true)
    private void postSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.PostSend event = new PacketEvent.PostSend(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "channelRead0", at = @At("TAIL"), cancellable = true)
    private void postChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.PostReceive event = new PacketEvent.PostReceive(packet);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(method = "closeChannel", at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;isOpen()Z", remap = false))
    public void doCloseChannel(ITextComponent message, CallbackInfo ci) {
        if(isChannelOpen()) Kisman.EVENT_BUS.post(new EventDisconnect(message));
    }
}
