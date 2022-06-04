package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.ShaderCharms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author TrollHack 0.0.2
 * @param <T>
 */
@Mixin(Render.class)
public class MixinRender<T extends Entity> {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    public void getTeamColor$Inject$HEAD(T entityIn, CallbackInfoReturnable<Integer> cir) {
        if(ShaderCharms.instance.isToggled() && ShaderCharms.instance.mode.checkValString("Outline2")) cir.setReturnValue(ShaderCharms.instance.getColor().getRGB());
    }
}
