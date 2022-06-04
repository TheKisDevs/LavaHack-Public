package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RangeVisualisator extends Module {
    private Setting mode = new Setting("Mode", this, "Sphere", new ArrayList<>(Arrays.asList("Sphere", "Circle")));

    private Setting radius = new Setting("Radius", this, 4.5, 0.1, 8, false);
    private Setting lineWidth = new Setting("LineWidth", this, 1.5, 0.1, 5, false);
    private Setting own = new Setting("Own", this, "None", new ArrayList<>(Arrays.asList("None", "Sphere", "Circle")));
    private Setting raytrace = new Setting("RayTrace", this, true);

    public RangeVisualisator() {
        super("RangeVisualisator", "RangeVisualisator", Category.RENDER);

        setmgr.rSetting(mode);
        setmgr.rSetting(radius);
        setmgr.rSetting(lineWidth);
        setmgr.rSetting(own);
        setmgr.rSetting(raytrace);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if(!mode.getValString().equalsIgnoreCase("None")) {
            render(event);
        }

        if(mode.getValString().equalsIgnoreCase("None") && !own.getValString().equalsIgnoreCase("None")) {
            render();
        }
    }

    private void render(RenderWorldLastEvent event) {
        for (EntityPlayer en : mc.world.playerEntities) {
            if (en.isEntityEqual(mc.player) && own.getValString().equalsIgnoreCase("None")) {
                continue;
            }

            if(mode.getValString().equalsIgnoreCase("Sphere") || own.getValString().equalsIgnoreCase("Sphere")) {
                int lines = 600 / Math.round(Math.max((mc.player.getDistance(en)), 1));
                lines = Math.min(lines, 25);
                double xPos = (en.lastTickPosX + (en.posX - en.lastTickPosX) * mc.timer.renderPartialTicks)
                        - mc.getRenderManager().renderPosX;
                double yPos = en.getEyeHeight()
                        + (en.lastTickPosY + (en.posY - en.lastTickPosY) * mc.timer.renderPartialTicks)
                        - mc.getRenderManager().renderPosY;
                double zPos = (en.lastTickPosZ + (en.posZ - en.lastTickPosZ) * mc.timer.renderPartialTicks)
                        - mc.getRenderManager().renderPosZ;
                float range = 3.5f;

                if (mc.player.getDistance(en) >= range) {
                    if (mc.player.isOnSameTeam(en)) {
                        RenderUtil.drawSphere(0.5, 1, 0.5, 0.5, xPos, yPos, zPos, range, lines, lines, (float) lineWidth.getValDouble());
                    } else {
                        RenderUtil.drawSphere(1, 0.8, 0.4, 0.5, xPos, yPos, zPos, range, lines, lines, (float) lineWidth.getValDouble());
                    }

                } else {
                    if (mc.player.isOnSameTeam(en)) {
                        RenderUtil.drawSphere(1, 0.4, 0.6, 0.7, xPos, yPos, zPos, range, lines, lines, (float) lineWidth.getValDouble());
                    } else {
                        RenderUtil.drawSphere(1, 0.6, 0.4, 0.7, xPos, yPos, zPos, range, lines, lines, (float) lineWidth.getValDouble());
                    }
                }
            } else if(mode.getValString().equalsIgnoreCase("Circle") || own.getValString().equalsIgnoreCase("Circle")) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();
                GlStateManager.enableDepth();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                final RenderManager renderManager = mc.getRenderManager();
                float hue = System.currentTimeMillis() % 7200L / 7200.0f;
                Color color = new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));
                final ArrayList<Vec3d> hVectors = new ArrayList<>();
                final double x = en.lastTickPosX + (en.posX - en.lastTickPosX) * event.getPartialTicks() - renderManager.renderPosX;
                final double y = en.lastTickPosY + (en.posY - en.lastTickPosY) * event.getPartialTicks() - renderManager.renderPosY;
                final double z = en.lastTickPosZ + (en.posZ - en.lastTickPosZ) * event.getPartialTicks() - renderManager.renderPosZ;
                GL11.glLineWidth((float) this.lineWidth.getValDouble());
                GL11.glBegin(1);
                for (int i = 0; i <= 360; ++i) {
                    final Vec3d vec = new Vec3d(x + Math.sin(i * 3.141592653589793 / 180.0) * this.radius.getValDouble(), y + 0.1, z + Math.cos(i * 3.141592653589793 / 180.0) * this.radius.getValDouble());
                    final RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(en.posX, en.posY + en.getEyeHeight(), en.posZ), vec, false, false, true);
                    if (result != null && this.raytrace.getValBoolean()) {
                        Kisman.LOGGER.info("raytrace was not null");
                        hVectors.add(result.hitVec);
                    }
                    else {
                        hVectors.add(vec);
                    }
                }
                for (int j = 0; j < hVectors.size() - 1; ++j) {
                    GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                    GL11.glVertex3d(hVectors.get(j).x, hVectors.get(j).y, hVectors.get(j).z);
                    GL11.glVertex3d(hVectors.get(j + 1).x, hVectors.get(j + 1).y, hVectors.get(j + 1).z);
                    color = new Color(Color.HSBtoRGB(hue += 0.0027777778f, 1.0f, 1.0f));
                }
                GL11.glEnd();
                GlStateManager.resetColor();
                GlStateManager.disableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }
}
