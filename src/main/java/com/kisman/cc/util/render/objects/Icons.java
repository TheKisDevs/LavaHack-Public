package com.kisman.cc.util.render.objects;

import com.kisman.cc.util.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public enum Icons implements Globals {
    CHECKED_CHECKBOX(new ResourceLocation("kismancc:icons/enabled1.png")),
    LOGO(new ResourceLocation("kismancc:icons/logo.png")),
    LOGO_NEW(new ResourceLocation("kismancc:icons/sex.png"));

    public final ResourceLocation resourceLocation;

    Icons(ResourceLocation resourceLocation) {this.resourceLocation = resourceLocation;}

    public void render(double x, double y, double width, double height) {
        render(x, y, width, height, new Colour(-1));
    }

    public void render(double x, double y, double width, double height, Colour color) {
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(resourceLocation);
        GL11.glColor4f(color.r1, color.g1, color.b1, color.a1);
        Render2DUtil.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glPopMatrix();
    }
}
