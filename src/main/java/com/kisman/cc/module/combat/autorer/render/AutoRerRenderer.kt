package com.kisman.cc.module.combat.autorer.render

import com.kisman.cc.module.combat.autorer.AutoRerUtil
import com.kisman.cc.module.combat.autorer.PlaceInfo
import com.kisman.cc.module.combat.autorer.util.Easing
import com.kisman.cc.module.combat.autorer.util.ProjectionUtils
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.customfont.CustomFontUtil
import com.kisman.cc.util.render.objects.TextOnBlockObject
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class AutoRerRenderer {
    @JvmField
    var lastBlockPos: BlockPos? = null

    @JvmField
    var prevPos: Vec3d? = null

    @JvmField
    var currentPos: Vec3d? = null

    @JvmField
    var lastRenderPos: Vec3d? = null

    @JvmField
    var lastUpdateTime = 0L

    @JvmField
    var startTime = 0L

    @JvmField
    var scale = 0.0f

    @JvmField
    var lastSelfDamage = 0.0f

    @JvmField
    var lastTargetDamage = 0.0f

    fun reset() {
        lastBlockPos = null
        prevPos = null
        currentPos = null
        lastRenderPos = null
        lastUpdateTime = 0L
        startTime = 0L
        scale = 0.0f
        lastSelfDamage = 0.0f
        lastTargetDamage = 0.0f
    }

    fun onRenderWorld(movingLength: Float, fadeLength: Float, renderer : RenderingRewritePattern, placeInfo : PlaceInfo, text : Boolean) {
        update(placeInfo)

        prevPos?.let { prevPos ->
            currentPos?.let { currentPos ->
                val multiplier = Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))
                scale = if (placeInfo != null) {
                    Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
                } else {
                    Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
                }

                renderer.draw(toRenderBox(renderPos, scale))

                lastRenderPos = renderPos


                //Text
                if(text && placeInfo != null) {
                    val text_ = buildString {
                        append("%.1f".format(lastTargetDamage))
                        if (this.isNotEmpty()) append('/')
                        append("%.1f".format(lastSelfDamage))
                    }

                    TextOnBlockObject(
                            text_,
                            placeInfo.blockPos,
                            (if (scale == 1.0f) Colour(255, 255, 255) else Colour(255, 255, 255, (255.0f * scale).toInt()))
                    )
                }
            }
        }
    }

    private inline fun toRenderBox(vec3d: Vec3d, scale: Float): AxisAlignedBB {
        val halfSize = 0.5 * scale
        return AxisAlignedBB(
            vec3d.x - halfSize + 0.5, vec3d.y - halfSize + 0.5, vec3d.z - halfSize + 0.5,
            vec3d.x + halfSize + 0.5, vec3d.y + halfSize + 0.5, vec3d.z + halfSize + 0.5
        )
    }

    fun update(placeInfo: PlaceInfo) {
        val newBlockPos = placeInfo?.blockPos
        if (newBlockPos != lastBlockPos) {
            currentPos = AutoRerUtil.toVec3dCenter(placeInfo.blockPos)
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastBlockPos == null) startTime = System.currentTimeMillis()

            lastBlockPos = newBlockPos
        }
        if(placeInfo != null) {
            lastSelfDamage = placeInfo.selfDamage
            lastTargetDamage = placeInfo.targetDamage
        }
    }
}