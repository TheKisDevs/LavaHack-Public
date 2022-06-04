package com.kisman.cc.mixin.mixins.viaforge;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import com.kisman.cc.viaforge.ViaForge;
import com.kisman.cc.viaforge.handler.*;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.NetworkManager$5")
public class MixinNetworkManagerChInit {
    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel && ViaForge.getInstance().getVersion() != ViaForge.SHARED_VERSION) {
            UserConnection user = new UserConnectionImpl(channel, true);
            new ProtocolPipelineImpl(user);
            channel.pipeline().addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new EncodeHandler(user)).addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new DecodeHandler(user));
        }
    }
}
