package com.kisman.cc.gui.halq.util

class TextUtil {
    companion object {
        val doubleRegex = "[0-9]*+.?+[0-9]*"

        fun isNumberChar(char: Char): Boolean {
            return char == '1' || char == '2' || char == '3' || char == '4' || char == '5' || char == '6' || char == '7' || char == '8' || char == '9' || char == '0'
        }

        fun parseNumber(value: String): Double {
            if(value.contains('.') || value.contains(',')) {
                if(value.endsWith(",") || value.endsWith(".")) return Integer.parseInt(value.substring(0, value.length - 1).replace(".", ",")).toDouble()
                return java.lang.Double.parseDouble(value.replace(".", ","))
            } else {
                return try {
                    Integer.parseInt(value).toDouble()
                } catch (e: NumberFormatException) {
                    0.0
                }
            }
        }
    }
}