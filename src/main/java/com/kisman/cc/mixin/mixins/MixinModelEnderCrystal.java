package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.CrystalModifier;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.*;

@Mixin(value = ModelEnderCrystal.class, priority = 10000)
public class MixinModelEnderCrystal {
    @Final @Shadow private ModelRenderer cube;
    @Final @Shadow private ModelRenderer glass;
    @Shadow private ModelRenderer base;

    /**
     * @author _kisman_
     */
    @Overwrite
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.translate(0.0f, -0.5f, 0.0f);
        if (base != null) base.render(scale);
        GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0f, (0.8f + ageInTicks), 0.0f);
        GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
        if (!CrystalModifier.instance.isToggled() || CrystalModifier.instance.outsideCube.getValBoolean()) glass.render(scale);
        GlStateManager.scale(0.875f, 0.875f, 0.875f);
        GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
        if (!CrystalModifier.instance.isToggled() || CrystalModifier.instance.outsideCube2.getValBoolean()) glass.render(scale);
        GlStateManager.scale(0.875f, 0.875f, 0.875f);
        GlStateManager.rotate(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotate(limbSwingAmount, 0.0f, 1.0f, 0.0f);
        if (!CrystalModifier.instance.isToggled() || CrystalModifier.instance.insideCube.getValBoolean()) cube.render(scale);
        GlStateManager.popMatrix();
    }
}
