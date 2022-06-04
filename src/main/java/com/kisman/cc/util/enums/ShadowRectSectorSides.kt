package com.kisman.cc.util.enums

import com.kisman.cc.util.render.objects.CornerObject
import com.sun.javafx.geom.Vec2d

enum class ShadowRectSectorSides(
    val parentSides: List<RectSides>,
    val corner: CornerObject
) {
    LeftTop(listOf(RectSides.Left, RectSides.Top), CornerObject(0, 90)),
    RightTop(listOf(RectSides.Right, RectSides.Top), CornerObject(90, 180)),
    RightBottom(listOf(RectSides.Right, RectSides.Bottom), CornerObject(180 , 270)),
    LeftBottom(listOf(RectSides.Left, RectSides.Bottom), CornerObject(270, 360));

    companion object {
        fun findSides(side: RectSides) : List<ShadowRectSectorSides> {
            var list : List<ShadowRectSectorSides> = ArrayList()
            for(sectorSide in values()) {
                if(sectorSide.parentSides.contains(side)) list = list + sectorSide
            }
            return list
        }

        fun getRenderPos(start : Vec2d, end : Vec2d, side : ShadowRectSectorSides) : Vec2d? {
            val corner = side.corner
            return if(corner.corner1 == 0) start else if(corner.corner1 == 90) Vec2d(end.x, start.y) else if(corner.corner1 == 180) end else if(corner.corner1 == 270) Vec2d(start.x, end.y) else null
        }
    }
}