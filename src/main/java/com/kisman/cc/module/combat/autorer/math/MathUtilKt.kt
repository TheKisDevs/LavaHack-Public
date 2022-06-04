package com.kisman.cc.module.combat.autorer.math

class MathUtilKt {
    companion object {
        fun cubic(number: Int): Double {
            return (number * number * number).toDouble()
        }

        fun cubic(number: Float): Double {
            return (number * number * number).toDouble()
        }

        fun cubic(number: Double): Double {
            return number * number * number
        }

        fun quart(number: Int): Double {
            return (number * number).toDouble()
        }

        fun quart(number: Float): Double {
            return (number * number).toDouble()
        }

        fun quart(number: Double): Double {
            return number * number
        }

        fun ceilToInt(number: Double): Int {
            return kotlin.math.ceil(number).toInt()
        }
    }
}