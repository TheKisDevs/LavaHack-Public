package com.kisman.cc.util.customfont

import com.kisman.cc.Kisman
import com.kisman.cc.util.customfont.norules.CFontRenderer
import net.minecraft.client.Minecraft

class CustomFontUtilKt {
    companion object {
        fun getCustomFont(name: String, gui: Boolean): Any? {
            return when(name) {
                "Verdana" -> Kisman.instance.customFontRenderer
                "Comfortaa" -> CustomFontUtil.comfortaa18
                "Comfortaa Light" -> CustomFontUtil.comfortaal18
                "Comfortaa Bold" -> CustomFontUtil.comfortaab18
                "Consolas" -> if(gui) CustomFontUtil.consolas15 else CustomFontUtil.consolas18
                "LexendDeca" -> CustomFontUtil.lexendDeca18
                "Futura" -> CustomFontUtil.futura20
                "SfUi" -> CustomFontUtil.sfui19
                else -> null
            }
        }

        fun getCustomFont(name: String): Any? {
            return getCustomFont(name, false)
        }

        fun getStringWidth(name: String, text: String, gui: Boolean): Int {
            if(name == null) return Minecraft.getMinecraft().fontRenderer.getStringWidth(text)
            val font = getCustomFont(name, gui);
            return if(font is CFontRenderer) font.getStringWidth(text) else if (font is CustomFontRenderer) font.getStringWidth(text) else 0
        }

        fun getStringWidth(name: String, text: String): Int {
            return getStringWidth(name, text, false)
        }

        fun getHeight(name: String, gui: Boolean): Int {
            if(name == null) return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
            val font = getCustomFont(name, gui);
            return if(font is CFontRenderer) (font.fontHeight - 8) / 2 else if (font is CustomFontRenderer) font.fontHeight / 2 - 1 else 0
        }

        fun getHeight(name: String): Int {
            return getHeight(name, false)
        }

        fun setAntiAliasAndFractionalMetrics(antiAlias: Boolean, fractionalMetrics: Boolean) {
            val font = getCustomFont(CustomFontUtil.getCustomFontName());
            if(font is CFontRenderer) {
                font.setAntiAlias(antiAlias)
                font.fractionalMetrics = fractionalMetrics
            } else if (font is CustomFontRenderer) {
                font.setAntiAlias(antiAlias)
                font.fractionalMetrics = fractionalMetrics
            }
        }

        fun setAntiAlias(antiAlias: Boolean) {
            val font = getCustomFont(CustomFontUtil.getCustomFontName());
            if(font is CFontRenderer) {
                font.setAntiAlias(antiAlias)
            } else if (font is CustomFontRenderer) {
                font.setAntiAlias(antiAlias)
            }
        }

        fun setFractionalMetrics(fractionalMetrics: Boolean) {
            val font = getCustomFont(CustomFontUtil.getCustomFontName());
            if(font is CFontRenderer) {
                font.fractionalMetrics = (fractionalMetrics)
            } else if (font is CustomFontRenderer) {
                font.fractionalMetrics = (fractionalMetrics)
            }
        }

        fun getAntiAlias(): Boolean {
            val font = getCustomFont(CustomFontUtil.getCustomFontName());
            if(font is CFontRenderer) {
                return font.antiAlias
            } else if (font is CustomFontRenderer) {
                return font.antiAlias
            }
            return false;
        }

        fun getFractionMetrics(): Boolean {
            val font = getCustomFont(CustomFontUtil.getCustomFontName());
            if(font is CFontRenderer) {
                return font.fractionalMetrics
            } else if (font is CustomFontRenderer) {
                return font.fractionalMetrics
            }
            return false;
        }
    }
}
