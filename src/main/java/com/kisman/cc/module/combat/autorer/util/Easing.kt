package com.kisman.cc.module.combat.autorer.util

import com.kisman.cc.module.combat.autorer.math.MathUtilKt
import com.kisman.cc.util.MathUtil

enum class Easing {
    OUT_QUART {
        override val opposite: Easing
            get() = OUT_QUART

        override fun inc0(x: Float): Float {
            return (1.0f - MathUtilKt.quart(1.0f - x)).toFloat()
        }
    },
    OUT_CUBIC {
        override val opposite: Easing
            get() = IN_CUBIC

        override fun inc0(x: Float): Float {
            return (1.0f - MathUtilKt.cubic(1.0f - x)).toFloat()
        }
    },
    IN_CUBIC {
        override val opposite: Easing
            get() = OUT_CUBIC

        override fun inc0(x: Float): Float {
            return MathUtilKt.cubic(x).toFloat()
        }
    };

    fun incOrDecOpposite(x: Float, min: Float, max: Float): Float {
        val delta = when {
            max == min -> return min
            max > min -> inc(x)
            else -> opposite.inc(x)
        }
        return MathUtil.lerp(min.toDouble(), max.toDouble(), delta.toDouble()).toFloat()
    }

    fun incOrDec(x: Float, min: Float, max: Float): Float {
        return MathUtil.lerp(min.toDouble(), max.toDouble(), inc(x).toDouble()).toFloat()
    }

    @Suppress("NAME_SHADOWING")
    fun inc(x: Float, min: Float, max: Float): Float {
        var min = min
        var max = max

        if (max == min) {
            return 0.0f
        } else if (max < min) {
            val oldMax = max
            max = min
            min = oldMax
        }

        if (x <= 0.0f) {
            return min
        } else if (x >= 1.0f) {
            return max
        }

        return MathUtil.lerp(min.toDouble(), max.toDouble(), inc0(x).toDouble()).toFloat()
    }

    fun inc(x: Float, max: Float): Float {
        if (max == 0.0f) {
            return 0.0f
        }

        if (x <= 0.0f) {
            return 0.0f
        } else if (x >= 1.0f) {
            return max
        }

        return inc0(x) * max
    }

    fun inc(x: Float): Float {
        if (x <= 0.0f) {
            return 0.0f
        } else if (x >= 1.0f) {
            return 1.0f
        }

        return inc0(x)
    }

    @Suppress("NAME_SHADOWING")
    fun dec(x: Float, min: Float, max: Float): Double {
        var min = min
        var max = max

        if (max == min) {
            return 0.0
        } else if (max < min) {
            val oldMax = max
            max = min
            min = oldMax
        }

        if (x <= 0.0f) {
            return max.toDouble()
        } else if (x >= 1.0f) {
            return min.toDouble()
        }

        return MathUtil.lerp(min.toDouble(), max.toDouble(), dec0(x).toDouble())
    }

    fun dec(x: Float, max: Float): Float {
        if (max == 0.0f) {
            return 0.0f
        }

        if (x <= 0.0f) {
            return max
        } else if (x >= 1.0f) {
            return 0.0f
        }

        return dec0(x) * max
    }

    fun dec(x: Float): Float {
        if (x <= 0.0f) {
            return 1.0f
        } else if (x >= 1.0f) {
            return 0.0f
        }

        return dec0(x)
    }

    abstract val opposite: Easing;

    protected abstract fun inc0(x: Float): Float

    private fun dec0(x: Float): Float {
        return 1.0f - inc0(x);
    }

    companion object {
        @JvmStatic
        fun toDelta(start: Long, length: Int): Float {
            return toDelta(start, length.toFloat())
        }

        @JvmStatic
        fun toDelta(start: Long, length: Long): Float {
            return toDelta(start, length.toFloat())
        }

        @JvmStatic
        fun toDelta(start: Long, length: Float): Float {
            return (toDelta(start).toFloat() / length).coerceIn(0.0f, 1.0f)
        }

        @JvmStatic
        fun toDelta(start: Long): Long {
            return System.currentTimeMillis() - start
        }
    }
}