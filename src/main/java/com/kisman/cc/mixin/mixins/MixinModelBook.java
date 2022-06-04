package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.model.ModelBook;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBook.class)
public class MixinModelBook {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderCallback(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.book.getValBoolean()) ci.cancel();
    }
}
