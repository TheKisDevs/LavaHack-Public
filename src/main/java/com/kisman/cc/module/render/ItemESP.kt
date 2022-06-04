package com.kisman.cc.module.render

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.module.combat.autorer.util.ProjectionUtils
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.objects.TextOnEntityObject
import i.gishreloaded.gishcode.utils.visual.ColorUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author Gerald(Man)/Dallas
 */
class ItemESP : Module("ItemESP", "Renders a items name and quantity ft Dallas", Category.RENDER) {
    val scale = Setting("Scale", this, 1.0, 0.0, 5.0, true)
    val count = Setting("Count", this, false)
    val astolfo = Setting("Astolfo", this, false)

    init {
        setmgr.rSetting(scale)
        setmgr.rSetting(count)
        setmgr.rSetting(astolfo)
    }

    @SubscribeEvent fun onRender(event: RenderGameOverlayEvent.Text) {
        for(entity in mc.world.loadedEntityList) {
            if(entity is EntityItem) {
                if(entity.item.displayName.length > 50) continue
                
                val text = (if (count.valBoolean) if (entity.item.count == 1) "" else "x" + entity.item.count.toString() + " " else "") + entity.item.displayName

                TextOnEntityObject(text, entity, Colour(255, 255, 255, 255)).draw(event.partialTicks)
/*
                val deltaX = MathHelper.clampedLerp(
                    entity.lastTickPosX, entity.posX,
                    event.partialTicks.toDouble()
                )
                val deltaY = MathHelper.clampedLerp(
                    entity.lastTickPosY, entity.posY,
                    event.partialTicks.toDouble()
                )
                val deltaZ = MathHelper.clampedLerp(
                    entity.lastTickPosZ, entity.posZ,
                    event.partialTicks.toDouble()
                )
                val projection: Vec3d =
                    ProjectionUtils.toScaledScreenPos(Vec3d(deltaX, deltaY, deltaZ).add(Vec3d(0.0, 0.25, 0.0)))
                GlStateManager.pushMatrix()
                GlStateManager.translate(projection.x, projection.y, 0.0)
                GlStateManager.scale(scale.valFloat, scale.valFloat, 0.0f)
                mc.fontRenderer.drawStringWithShadow(
                    (if (count.valBoolean) if (entity.item.count == 1) "" else "x" + entity.item.count
                        .toString() + " " else "") + entity.item.displayName,
                    -(mc.fontRenderer.getStringWidth(
                        (if (count.valBoolean) if (entity.item.count == 1) "" else "x" + entity.item
                            .count.toString() + " " else "") + entity.item.displayName
                    ) / 2.0f),
                    (-mc.fontRenderer.FONT_HEIGHT).toFloat(),
                    (if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else -1)
                )
                GlStateManager.popMatrix()*/
            }
        }
    }

    override fun isVisible(): Boolean {
        return false
    }

    override fun isBeta(): Boolean {
        return true
    }
}