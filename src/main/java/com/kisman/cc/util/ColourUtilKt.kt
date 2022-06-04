package com.kisman.cc.util

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.text.TextFormatting
import java.awt.Color

class ColourUtilKt {
    companion object {
        fun getDefaultColor(): Colour {
            return Colour(255, 255, 255, 255)
        }

        fun toConfig(color: Colour): String {
            return "${color.r}:${color.g}:${color.b}:${color.a}"
        }

        fun fromConfig(config: String, color: Colour): Colour {
            val split = config.split(':')
            return try {
                Colour(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]))
            } catch (e: NumberFormatException) {
                color
            }
        }

        fun healthColor(entity: EntityLivingBase): TextFormatting {
            if (entity.health + entity.getAbsorptionAmount() > 14.0f) {
                return TextFormatting.GREEN;
            }
            if (entity.health + entity.getAbsorptionAmount() > 6.0f) {
                return TextFormatting.YELLOW;
            }
            return TextFormatting.RED;
        }
    }

    class BlockColors {
        companion object {
            fun getChestColor(): Colour {
                return Colour(Color(0.94f, 0.6f, 0.11f))
            }

            fun getEnderChestColor(): Colour {
                return Colour(Color(0.53f, 0.11f, 0.94f))
            }

            fun getShulkerBoxColor(): Colour {
                return Colour(Color(0.8f, 0.08f, 0.93f))
            }

            fun getDispenserColor(): Colour {
                return Colour(Color(0.34f, 0.32f, 0.34f))
            }

            fun getFurnaceColor(): Colour {
                return Colour(Color(0.34f, 0.32f, 0.34f))
            }

            fun getHopperColor(): Colour {
                return Colour(Color(0.53f, 0.11f, 0.34f))
            }

            fun getDropperColor(): Colour {
                return Colour(Color(0.34f, 0.32f, 0.34f))
            }
        }
    }
}