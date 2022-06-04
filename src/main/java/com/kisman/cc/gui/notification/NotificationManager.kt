package com.kisman.cc.gui.notification/*
package com.kisman.cc.gui.notification

import com.kisman.cc.module.combat.autorer.util.Easing
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Render2DUtil
import com.kisman.cc.util.customfont.CustomFontUtil
import i.gishreloaded.gishcode.utils.TimerUtils
import org.lwjgl.opengl.GL11.glTranslatef
import java.util.function.Supplier

internal object NotificationManager {
    class AbstractNotification(
        val header : String,
        val text : String,
        val primaryColor : Colour,
        val backgroundColor : Colour,
        val delay : Long,
        val left : Supplier<Boolean>
    ) {
        val timer : TimerUtils = TimerUtils()

        init {
            timer.reset()
        }

        fun draw() {}
    }

    private class Message(
        private var message: String,
        private var length: Long,
        val id: Int,
        val left: Supplier<Boolean>,
        val color : Supplier<Colour>
    ) {
        private val startTime by lazy { System.currentTimeMillis() }
        val isTimeout get() = System.currentTimeMillis() - startTime > length

        private val width0 = FrameValue {
            max(minWidth, padding + padding + MainFontRenderer.getWidth(message) + padding)
        }
        private val width by width0

        fun update(message: String, length: Long) {
            this.message = message
            this.length = length + (System.currentTimeMillis() - startTime)
            width0.updateLazy()
        }

        fun render(): Float {
            if (!left.get() && width > minWidth) {
                glTranslatef(minWidth - width, 0.0f, 0.0f)
            }

            return when (val deltaTotal = Easing.toDelta(startTime)) {
                in 0L..299L -> {
                    val delta = deltaTotal / 300.0f
                    val progress = Easing.OUT_CUBIC.inc(delta)
                    renderStage1(progress)
                }
                in 300L..500L -> {
                    val delta = (deltaTotal - 300L) / 200.0f
                    val progress = Easing.OUT_CUBIC.inc(delta)
                    renderStage2(progress)
                }
                else -> {
                    if (deltaTotal < length) {
                        renderStage3()
                    } else {
                        when (val endDelta = deltaTotal - length) {
                            in 0L..199L -> {
                                val delta = (endDelta) / 200.0f
                                val progress = Easing.OUT_CUBIC.dec(delta)
                                renderStage2(progress)
                            }
                            in 200L..500L -> {
                                val delta = (endDelta - 200L) / 300.0f
                                val progress = Easing.OUT_CUBIC.dec(delta)
                                renderStage1(progress)
                            }
                            else -> {
                                -1.0f
                            }
                        }
                    }
                }
            }
        }

        private fun renderStage1(progress: Float): Float {
            if (left.get()) {
                Render2DUtil.drawRectWH(
                    0.0,
                    0.0,
                    width * progress,
                    height.toDouble(),
                    color.get().rgb
                )
            } else {
                Render2DUtil.drawRectWH(
                    (minWidth * (1 - progress)).toDouble(),
                    0.0,
                    width,
                    height.toDouble(),
                    color.get().rgb
                )
            }

            return (height + space) * progress
        }

        private fun renderStage2(progress: Float): Float {
            Render2DUtil.drawRectWH(
                0.0,
                0.0,
                width,
                height.toDouble(),
                backGroundColor.rgb
            )

            val textColor = Colour(255, 255, 255, (255.0f * progress).toInt())
            CustomFontUtil.drawStringWithShadow(
                message,
                stringPosX.toDouble(),
                stringPosY.toDouble(),
                textColor.rgb
            )

            if (left.get()) {
                Render2DUtil.drawRectWH(
                    (width - padding) * progress,
                    0.0,
                    width,
                    height.toDouble(),
                    color.get().rgb
                )
            } else {
                Render2DUtil.drawRectWH(
                    0.0,
                    0.0,
                    padding + (width - padding) * (1 - progress),
                    height.toDouble(),
                    color.get().rgb
                )
            }

            return height + space
        }

        private fun renderStage3(): Float {
            Render2DUtil.drawRectWH(
                0.0,
                0.0,
                width,
                height.toDouble(),
                backGroundColor.rgb
            )

            if (left.get()) {
                Render2DUtil.drawRectWH(
                    width - padding,
                    0.0,
                    width,
                    height.toDouble(),
                    backGroundColor.rgb
                )
            } else {
                Render2DUtil.drawRectWH(
                    0.0,
                    0.0,
                    padding.toDouble(),
                    height.toDouble(),
                    backGroundColor.rgb
                )
            }

            CustomFontUtil.drawStringWithShadow(
                message,
                stringPosX.toDouble(),
                stringPosY.toDouble(),
                -1
            )

            return height + space
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Message

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            return id
        }

        companion object {
            val backGroundColor get() = Colour(0, 0, 0, 120)

            val minWidth get() = 128.0f

            val height get() = 24.0f
            val space get() = 4.0f

            val padding get() = 4.0f
            val stringPosX get() = if (false) padding else padding + padding
            val stringPosY get() = height * 0.5f - 1.0f - CustomFontUtil.getFontHeight() * 0.5f
        }
    }
}*/
