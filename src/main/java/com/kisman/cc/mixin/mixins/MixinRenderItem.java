package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.render.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.awt.*;

@Mixin(value = RenderItem.class, priority = 10000)
public class MixinRenderItem {
    @Shadow private void renderModel(IBakedModel model, int color, ItemStack stack) {}
    @Shadow private void renderEffect(IBakedModel model) {}

    @ModifyArg(method = "renderEffect", at = @At(value="INVOKE", target="net/minecraft/client/renderer/RenderItem.renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index=1)
    private int renderEffect(int oldValue) {
        return ViewModel.instance.isToggled() && ViewModel.instance.useAlpha.getValBoolean() ? new Color(255, 255, 255, ViewModel.instance.alpha.getValInt()).getRGB() : oldValue;
    }

    /**
     * @author _kisman_
     * @reason NoRender -> Enchant Glint
     */
    @Overwrite
    public void renderItem(ItemStack stack, IBakedModel model) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            if (model.isBuiltInRenderer()) {
                if (ViewModel.instance.isToggled() && ViewModel.instance.useAlpha.getValBoolean()) GlStateManager.color(1, 1, 1, ViewModel.instance.alpha.getValFloat() / 255f);
                else GlStateManager.color(1, 1, 1, 1);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            } else {
                renderModel(model, ViewModel.instance.isToggled() && ViewModel.instance.useAlpha.getValBoolean() ? new Color(255, 255, 255, ViewModel.instance.alpha.getValInt()).getRGB() : -1, stack);
                if (stack.hasEffect()) {
                    if(NoRender.instance.isToggled() && NoRender.instance.enchantGlint.getValBoolean()) {} else renderEffect(model);
                }
            }

            GlStateManager.popMatrix();
        }
    }
}
