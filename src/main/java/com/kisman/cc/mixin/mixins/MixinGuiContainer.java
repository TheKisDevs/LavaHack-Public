package com.kisman.cc.mixin.mixins;

import com.kisman.cc.gui.other.container.ItemESP;
import com.kisman.cc.module.render.ContainerModifier;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.*;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Set;

@Mixin(value = GuiContainer.class, priority = 10000)
public class MixinGuiContainer extends GuiScreen {
    @Shadow protected int guiLeft, guiTop, xSize, ySize;
    @Shadow public Container inventorySlots;
    @Shadow private ItemStack draggedStack;
    @Shadow private Slot clickedSlot;
    @Shadow private boolean isRightMouseClick;
    @Shadow protected boolean dragSplitting;
    @Shadow @Final protected Set<Slot> dragSplittingSlots;
    @Shadow private void updateDragSplitting() {}
    @Shadow private int dragSplittingLimit;
    @Shadow protected boolean checkHotbarKeys(int keyCode) {return false;}
    @Shadow private Slot hoveredSlot;
    @Shadow protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {}

    public ItemESP itemESP = new ItemESP();

    @Inject(method = "initGui", at = @At("RETURN"))
    private void doInitGui(CallbackInfo ci) {
        itemESP.init(guiLeft, guiTop, xSize, ySize);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void drawee(int mouseX, int mouseY, float particalTicks, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled() && ContainerModifier.instance.containerShadow.getValBoolean()) {
            if(ContainerModifier.instance.containerShadow.getValBoolean()) {
                {
                    double x = 0, y = (guiTop + xSize / 2) - guiLeft / 2, y2 = (guiTop + xSize / 2) + guiLeft / 2;
                    double x2 = guiLeft, y3 = guiTop, y4 = guiTop + ySize;

                    Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{x, y}, new double[]{x2, y3}, new double[]{x2, y4}, new double[]{x, y2}), Color.BLACK, new Color(0, 0, 0, 0), false));
                }

                {
                    double x = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth_double(), y = (guiTop + xSize / 2) - guiLeft / 2, y2 = (guiTop + xSize / 2) + guiLeft / 2;

                    Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[]{guiLeft + xSize, guiTop}, new double[]{x, y}, new double[]{x, y2}, new double[]{guiLeft + xSize, guiTop + ySize}), new Color(0, 0, 0, 0), Color.BLACK, false));
                }
            }
            if(ContainerModifier.instance.itemESP.getValBoolean()) itemESP.getGuiTextField().drawTextBox();
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void doDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        itemESP.getItemStacks().clear();
        if(!itemESP.getGuiTextField().getText().isEmpty()) for(Slot slot : inventorySlots.inventorySlots) if(slot.getHasStack() && slot.getStack().getDisplayName().toLowerCase().contains(itemESP.getGuiTextField().getText().toLowerCase()))  itemESP.getItemStacks().add(slot.getStack());
    }

    /**
     * @author _kisman_
     */
    @Overwrite
    protected void keyTyped(char typedChar, int keyCode) {
        if(ContainerModifier.instance.itemESP.getValBoolean()) itemESP.getGuiTextField().textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 1 || (this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) && !itemESP.getGuiTextField().isFocused())) {
            this.mc.player.closeScreen();
        }

        checkHotbarKeys(keyCode);
        if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
            } else if (this.mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void doMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled() && ContainerModifier.instance.itemESP.getValBoolean()) {
            itemESP.getGuiTextField().mouseClicked(mouseX, mouseY, mouseButton);
            if(itemESP.getGuiTextField().isFocused()) ci.cancel();
        }
    }
    
    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableDepth()V"))
    private void drawSlotHook(Slot slot, CallbackInfo ci) {
        if(ContainerModifier.instance.isToggled() && ContainerModifier.instance.itemESP.getValBoolean() && !itemESP.getItemStacks().isEmpty() && itemESP.getItemStacks().contains(slot.getStack())) drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, ColorUtils.astolfoColors(100, 100));
    }
}
