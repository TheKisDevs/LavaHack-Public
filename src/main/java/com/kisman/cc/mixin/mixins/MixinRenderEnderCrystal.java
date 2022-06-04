package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.CrystalModifier;
import com.kisman.cc.util.OutlineUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = RenderEnderCrystal.class, priority = 10000)
public class MixinRenderEnderCrystal {
    @Final @Shadow private ModelBase modelEnderCrystal;
    @Final @Shadow private ModelBase modelEnderCrystalNoBase;
    @Final @Shadow private static ResourceLocation ENDER_CRYSTAL_TEXTURES;

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void IdoRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.fancyGraphics = false;

        if(CrystalModifier.instance.isToggled()) {
            float var14 = (float)entity.innerRotation + partialTicks;

            GL11.glPushMatrix();

            //scale
            if(entity.equals(CrystalModifier.instance.preview.getEntity())) GL11.glScalef(1, 1, 1);
            else if(CrystalModifier.instance.scale.getValBoolean()) GL11.glScaled(CrystalModifier.instance.scaleVal.getValDouble(), -CrystalModifier.instance.scaleVal.getValDouble(), CrystalModifier.instance.scaleVal.getValDouble());

            //translate
            GL11.glTranslated(x + CrystalModifier.instance.translateX.getValDouble(), y + CrystalModifier.instance.translateY.getValDouble(), z + CrystalModifier.instance.translateZ.getValDouble());

            float var15 = MathHelper.sin(var14 * 0.2f) / 2 + 0.5f;
            var15 += var15 * var15;

            float spinSpeed = CrystalModifier.instance.speed.getValFloat();
            float bounceSpeed = CrystalModifier.instance.bounce.getValFloat();

            if(CrystalModifier.instance.texture.getValBoolean()) {
                if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
            }

            //polygon
            GL11.glPushAttrib(1048575);
            if(CrystalModifier.instance.mode.getValString().equals(CrystalModifier.Modes.Wireframe.name())) GL11.glPolygonMode(1032, 6913);

            //other
            if(CrystalModifier.instance.texture.getValBoolean()) mc.renderManager.renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);

            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glEnable(10754);

            //custom color
            if(CrystalModifier.instance.customColor.getValBoolean()) GL11.glColor4f(CrystalModifier.instance.crystalColor.getColour().r1, CrystalModifier.instance.crystalColor.getColour().g1, CrystalModifier.instance.crystalColor.getColour().b1, CrystalModifier.instance.crystalColor.getColour().a1);
            else GL11.glColor3f(1, 1, 1);
            if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
            else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);

            //ench texture
            GL11.glEnable(2929);
            GL11.glDepthMask(true);

            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();

            //outline
            if(CrystalModifier.instance.outline.getValBoolean()) {
                if(CrystalModifier.instance.outlineMode.getValString().equals(CrystalModifier.OutlineModes.Wire.name())) {
                    GL11.glPushAttrib(1048575);
                    GL11.glPolygonMode(1032, 6913);
                    GL11.glDisable(3008);
                    GL11.glDisable(3553);
                    GL11.glDisable(2896);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glLineWidth(CrystalModifier.instance.lineWidth.getValFloat());
                    GL11.glEnable(2960);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    GL11.glEnable(10754);
                    GL11.glColor4f(CrystalModifier.instance.color.getColour().r1, CrystalModifier.instance.color.getColour().g1, CrystalModifier.instance.color.getColour().b1, 1.0f);

                    if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);

                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);

                    if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);

                    GL11.glEnable(3042);
                    GL11.glEnable(2896);
                    GL11.glEnable(3553);
                    GL11.glEnable(3008);
                    GL11.glPopAttrib();
                } else {
                    OutlineUtils.setColor(CrystalModifier.instance.color);
                    OutlineUtils.renderOne((float) CrystalModifier.instance.lineWidth.getValDouble());

                    if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);

                    OutlineUtils.renderTwo();

                    if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);

                    OutlineUtils.renderThree();
                    OutlineUtils.renderFour();
                    OutlineUtils.setColor(CrystalModifier.instance.color);

                    if(entity.shouldShowBottom()) modelEnderCrystal.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);
                    else modelEnderCrystalNoBase.render(entity, 0, var14 * spinSpeed, var15 * bounceSpeed, 0, 0, 0.0625f);

                    OutlineUtils.renderFive();
                    OutlineUtils.setColor(Color.WHITE);
                }
            }
            GL11.glPopMatrix();

            ci.cancel();
        }
    }
}
