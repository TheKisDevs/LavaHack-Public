package com.kisman.cc.util.render.objects

import com.kisman.cc.module.client.CustomFontModule
import com.kisman.cc.util.Colour
import com.kisman.cc.util.customfont.CustomFontUtil
import com.kisman.cc.util.enums.Object3dTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11

//TODO: need to finish
class TextOnEntityObject(
        val text : String,
        val entity : Entity,
        override val color : Colour
) : Abstract3dObject() {
    override val type: Object3dTypes = Object3dTypes.Text

    override fun draw(ticks: Float) {
        GL11.glPushMatrix()

        glBillboardDistanceScaled(
                Box(entity.entityBoundingBox).center(),
                Minecraft.getMinecraft().player,
                1f
        )
        GlStateManager.disableDepth()
        if(CustomFontModule.turnOn) GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        color.glColor()
        GL11.glTranslated(
                (-(CustomFontUtil.getStringWidth(text) / 2)).toDouble(),
                0.0,
                0.0
        )

        CustomFontUtil.drawStringWithShadow(text, 0.0, 0.0, -1)

        GlStateManager.enableLighting()
        if(CustomFontModule.turnOn) GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()

        GL11.glPopMatrix()
    }
}