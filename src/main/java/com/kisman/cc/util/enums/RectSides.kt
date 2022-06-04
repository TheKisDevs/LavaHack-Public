package com.kisman.cc.util.enums

import com.kisman.cc.util.render.objects.RectObject
import com.sun.javafx.geom.Vec2d

enum class RectSides {
    Top, Bottom, Right, Left;

    companion object {
        fun getRenderPos(start: Vec2d, end: Vec2d, side : RectSides) : RectObject {
            return when (side) {
                Top -> RectObject(start, Vec2d(end.x, start.y))
                Bottom -> RectObject(Vec2d(start.x, end.y), end)
                Right -> RectObject(Vec2d(end.x, start.y), end)
                Left -> RectObject(start, Vec2d(start.x, end.y))
            }
        }
    }
}