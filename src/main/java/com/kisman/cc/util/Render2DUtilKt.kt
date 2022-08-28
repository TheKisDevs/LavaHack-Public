package com.kisman.cc.util

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.MathHelper

class Render2DUtilKt {
    companion object {
        fun getDeltas(ticks: Float, player: EntityPlayer): List<Double> {
            return listOf(
                MathHelper.clampedLerp(player.lastTickPosX, player.posX, ticks.toDouble()),
                MathHelper.clampedLerp(player.lastTickPosY, player.posY, ticks.toDouble()),
                MathHelper.clampedLerp(player.lastTickPosZ, player.posZ, ticks.toDouble())
            )
        }
    }
}