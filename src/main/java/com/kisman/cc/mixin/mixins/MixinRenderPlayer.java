package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Shadow public ResourceLocation getEntityTexture(AbstractClientPlayer abstractClientPlayer) {return null;}

    @Inject(method = "preRenderCallback*", at = @At("HEAD"))
    public void renderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        if(Spin.instance.isToggled()) {
            float f = 0.9357f;
            float hue = (float) (System.currentTimeMillis() % 22600L) / 5.0f;

            GlStateManager.scale(f, f, f);

            GlStateManager.rotate(hue, 1, 0, hue);
        } else if(Reverse.instance.isToggled() && !Spin.instance.isToggled()) GlStateManager.rotate(180, 1, 0, 0);
    }

    @Inject(method = "renderEntityName(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDLjava/lang/String;D)V", at = @At("HEAD"), cancellable = true)
    private void drawBigBebra(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo ci) {
        if(NameTags.instance.isToggled() && entityIn instanceof EntityPlayer) ci.cancel();
    }
}
