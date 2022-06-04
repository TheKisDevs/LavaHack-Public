package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem {
    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void doDoRender(EntityItem f4, double f5, double f7, double f9, float f6, float transformedModel, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.items.getValBoolean()) ci.cancel();
    }
}
