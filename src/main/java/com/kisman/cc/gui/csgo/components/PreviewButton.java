package com.kisman.cc.gui.csgo.components;

import com.kisman.cc.gui.csgo.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class PreviewButton extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;

    private Entity entity;
    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;

    private boolean opened;
    private int mouseX;
    private int mouseY;

    public PreviewButton(IRenderer renderer, int preferredWidth, int preferredHeight, Entity entity) {
        super(renderer);

        this.entity = entity;

        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;

        setWidth(preferredWidth);
        updateHeight();
    }

    public PreviewButton(IRenderer renderer, Entity entity) {
        this(renderer, PREFERRED_WIDTH, PREFERRED_HEIGHT, entity);
    }

    @Override
    public void render() {
        updateHeight();

        renderer.drawRect(x, y, getWidth(), getHeight(), Window.TERTIARY_FOREGROUND);

        if (hovered) renderer.drawRect(x, y, getWidth(), preferredHeight, Window.SECONDARY_FOREGROUND);

        renderer.drawRect(x + getWidth() - preferredHeight, y, preferredHeight, getHeight(), (hovered || opened) ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND);

        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, (hovered && !opened) ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        String text = entity != null ? entity.getName() : TextFormatting.RED + "ERROR";

        renderer.drawString(x + 4, y + preferredHeight / 2 - renderer.getStringHeight(text) / 2, text, Window.FOREGROUND);

        if (opened) {
            if(entity instanceof EntityEnderCrystal) {
                EntityEnderCrystal crystal = new EntityEnderCrystal(Minecraft.getMinecraft().world, Double.longBitsToDouble(Double.doubleToLongBits(9.310613315809524E306) ^ 0x7FAA847B55B02A7FL), Double.longBitsToDouble(Double.doubleToLongBits(1.7125394916952668E308) ^ 0x7FEE7BF580E967CDL), Double.longBitsToDouble(Double.doubleToLongBits(1.351057559302745E308) ^ 0x7FE80CB4154FF45AL));
                crystal.setShowBottom(false);
                crystal.rotationYaw = Float.intBitsToFloat(Float.floatToIntBits(1.1630837E38f) ^ 0x7EAF005B);
                crystal.rotationPitch = Float.intBitsToFloat(Float.floatToIntBits(2.1111544E38f) ^ 0x7F1ED35B);
                crystal.innerRotation = 0;
                crystal.prevRotationYaw = Float.intBitsToFloat(Float.floatToIntBits(3.176926E38f) ^ 0x7F6F015F);
                crystal.prevRotationPitch = Float.intBitsToFloat(Float.floatToIntBits(2.4984888E38f) ^ 0x7F3BF725);

                GL11.glScalef(Float.intBitsToFloat(Float.floatToIntBits(6.72125f) ^ 0x7F57147B), (float)Float.intBitsToFloat(Float.floatToIntBits(8.222657f) ^ 0x7E839001), (float)Float.intBitsToFloat(Float.floatToIntBits(7.82415f) ^ 0x7F7A5F70));
                drawEntityOnScreen((x + getWidth() / 2) / 2, (getHeight() + 2) / 2, 40, 0, 0, crystal);
            } else {
               final Entity entity = Minecraft.getMinecraft().player;
                drawEntityOnScreen(x + getWidth() / 2, getHeight() - 2, 40, 0, 0, entity);
            }
        }
    }

    @Override public void postRender() { }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + preferredHeight;

        mouseX = x;
        mouseY = y;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        if (button != 0) return false;
        if (hovered) {
            setOpened(!opened);
            updateHeight();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {

        return super.mouseReleased(button, x, y, offscreen);
    }

    public void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, Entity ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.setRenderYawOffset((float)Math.atan((double)(mouseX / 40.0F)) * 20.0F);
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.setRotationYawHead(ent.rotationYaw);
        ent.prevRotationYaw = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void updateHeight() {
        if (opened) setHeight(preferredHeight + 100);
        else setHeight(preferredHeight);
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
        updateHeight();
    }
}
