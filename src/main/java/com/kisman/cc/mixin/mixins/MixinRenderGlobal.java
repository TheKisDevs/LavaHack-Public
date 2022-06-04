package com.kisman.cc.mixin.mixins;

import com.kisman.cc.event.events.RenderEntityEvent;
import com.kisman.cc.module.render.*;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, priority = 10000)
public class MixinRenderGlobal {
    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    public void onDrawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, int execute, float partialTicks, CallbackInfo ci) {
        if(NoRender.instance.isToggled() && NoRender.instance.defaultBlockHighlight.getValBoolean()) ci.cancel();
    }

    @Inject(method = "renderEntities", at = @At("HEAD"))
    public void renderEntitiesHead(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        RenderEntityEvent.setRenderingEntities(true);
    }

    @Inject(method = "renderEntities", at = @At("RETURN"))
    public void renderEntitiesReturn(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        RenderEntityEvent.setRenderingEntities(false);
    }
}
