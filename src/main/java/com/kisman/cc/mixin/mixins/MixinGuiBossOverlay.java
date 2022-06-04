package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.NoRender;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void render(CallbackInfo ci) {
        if(NoRender.instance.bossBar.getValBoolean()) ci.cancel();
    }
}
