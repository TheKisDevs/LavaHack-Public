package com.kisman.cc.util.render.objects

import com.kisman.cc.util.Colour
import com.kisman.cc.util.Rendering
import com.kisman.cc.util.customfont.CustomFontUtil
import com.kisman.cc.util.enums.Object3dTypes
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

class TextOnBlockObject(
        val text : String,
        val pos : BlockPos,
        override val color : Colour
) : Abstract3dObject() {
    override val type : Object3dTypes = Object3dTypes.Text

    override fun draw(ticks: Float) {
        Rendering.setup()
        glBillboardDistanceScaled(
                Vec3d(
                        pos.x + 0.5,
                        pos.y + 0.5,
                        pos.z + 0.5
                ),
                Minecraft.getMinecraft().player,
                (2 - 0.3f)
        )

        GL11.glTranslated(
                (-(CustomFontUtil.getStringWidth(text) / 2)).toDouble(),
                0.0,
                0.0
        )

        CustomFontUtil.drawStringWithShadow(text, 0.0, 0.0, color.rgb)

        Rendering.release()
    }
}