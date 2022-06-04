package com.kisman.cc.settings.util

import com.kisman.cc.Kisman
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Render2DUtil
import com.kisman.cc.util.enums.GlowModes
import java.awt.Color
import java.util.function.Supplier

class GlowRendererPattern(val module: Module, val visible: Supplier<Boolean>) {
    val mode = Setting("Glow Mode", module, GlowModes.Default).setVisible {visible.get()}
    val offset = Setting("Glow Offset", module, 6.0, 1.0, 20.0, true).setVisible {mode.valEnum.equals(GlowModes.Default) && visible.get()}
    val radius = Setting("Glow Radius", module, 15.0, 0.0, 20.0, true).setVisible {mode.valEnum.equals(GlowModes.Shader) && visible.get()}
    val boxSize = Setting("Glow Box Size", module, 0.0, 0.0, 20.0, true).setVisible {mode.valEnum.equals(GlowModes.Shader) && visible.get()}

    fun init() {
        Kisman.instance.settingsManager.rSetting(mode)
        Kisman.instance.settingsManager.rSetting(offset)
        Kisman.instance.settingsManager.rSetting(radius)
        Kisman.instance.settingsManager.rSetting(boxSize)
    }

    fun draw(ticks: Float, color: Colour, x: Int, y: Int, width: Int, height: Int) {
        when(mode.valEnum as GlowModes) {
            GlowModes.Default -> {
                val offset = this.offset.valInt
                Render2DUtil.drawGlow(
                    x.toDouble(),
                    (y - offset).toDouble(),
                    (x + width).toDouble(),
                    (y + height + offset).toDouble(),
                color.rgb
                )
            }
            GlowModes.Shader ->
                drawRoundedRect(
                    (x / 2).toDouble(),
                    (y / 2).toDouble(),
                    ((x + width) / 2).toDouble(),
                    ((y + height) / 2).toDouble(),
                    color.color,
                    boxSize.valDouble
                )
        }
    }

    fun drawRoundedRect(startX: Double, startY: Double, endX: Double, endY: Double, color: Color, radius: Double) {
        Render2DUtil.drawRoundedRect(
            startX.toFloat() - radius,
            startY.toFloat() - radius,
            endX.toFloat() + radius,
            endY.toFloat() + radius,
            color.rgb,
            this.radius.valFloat
        )
    }
}