package com.kisman.cc.util.render.objects

import com.kisman.cc.util.Colour
import com.kisman.cc.util.Render2DUtil
import com.kisman.cc.util.enums.RectSides
import com.kisman.cc.util.enums.ShadowRectSectorSides

class ShadowRectObject(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    private val color1: Colour, //Color of rect
    private val color2: Colour, //Color of gradients & sectors
    val radius: Double,
    private val excludedSides: List<RectSides>
) {
    private var excludedSectorSides: List<ShadowRectSectorSides> = listOf()

    init {
        if(excludedSides.isNotEmpty()) {
            for(side in excludedSides) {
                excludedSectorSides = excludedSectorSides + (ShadowRectSectorSides.findSides(side))
            }
        }
    }

    constructor(x1: Double, y1: Double, x2: Double, y2: Double, color: Colour, radius: Double) : this(x1, y1, x2, y2, color, color, radius, ArrayList<RectSides>())
    constructor(x1: Double, y1: Double, x2: Double, y2: Double, color: Colour) : this(x1, y1, x2, y2, color, 5.0)
    constructor(x1: Double, y1: Double, x2: Double, y2: Double, color : Colour, excludedSides: List<RectSides>) : this(x1, y1, x2, y2, color, color, 5.0, excludedSides)

    fun draw() {
        Render2DUtil.drawRect(x1, y1, x2, y2, color1.rgb)

        /*for(side in ShadowRectSectorSides.values()) {
            if(excludedSectorSides.isNotEmpty() and excludedSectorSides.contains(side)) continue

            //renderer of the sector
            Render2DUtilKt.drawSector2(
                ShadowRectSectorSides.getRenderPos(
                    Vec2d(x1, y1),
                    Vec2d(x2, y2),
                    side
                ),
                side.corner.corner1,
                side.corner.corner2,
                radius.toInt()
            )
        }*/


        //renderer of the gradients
        if(!excludedSides.contains(RectSides.Top)) AbstractGradient.drawGradientRect(x1, y1 - radius, x2, y1, false, true, color1.rgb, color2.rgb)
        if(!excludedSides.contains(RectSides.Bottom)) AbstractGradient.drawGradientRect(x1, y2, x2, y2 + radius, false, false, color1.rgb, color2.rgb)
        if(!excludedSides.contains(RectSides.Left)) AbstractGradient.drawGradientRect(x1 - radius, y1, x1, y2, true, true, color1.rgb, color2.rgb)
        if(!excludedSides.contains(RectSides.Right)) AbstractGradient.drawGradientRect(x2, y1, x2 + radius, y2, true, false, color1.rgb, color2.rgb)
        if(!excludedSectorSides.contains(ShadowRectSectorSides.LeftTop)) Render2DUtil.drawPolygonPart(x1, y1, radius.toInt(), 0, color1.rgb, color2.rgb);
        if(!excludedSectorSides.contains(ShadowRectSectorSides.RightTop)) Render2DUtil.drawPolygonPart(x2, y1, radius.toInt(), 1, color1.rgb, color2.rgb);
        if(!excludedSectorSides.contains(ShadowRectSectorSides.LeftBottom)) Render2DUtil.drawPolygonPart(x1, y2, radius.toInt(), 2, color1.rgb, color2.rgb);
        if(!excludedSectorSides.contains(ShadowRectSectorSides.RightBottom)) Render2DUtil.drawPolygonPart(x2, y2, radius.toInt(), 3, color1.rgb, color2.rgb);

//        if(!excludedSectorSides.contains(ShadowRectSectorSides.LeftTop)) Render2DUtilKt.drawSector2(Vec2d(x1, y1), 0, 90, radius.toInt(), color2)
//        if(!excludedSectorSides.contains(ShadowRectSectorSides.RightTop)) Render2DUtilKt.drawSector2(Vec2d(x2, y1), 90, 180, radius.toInt(), color2)
//        if(!excludedSectorSides.contains(ShadowRectSectorSides.RightBottom)) Render2DUtilKt.drawSector2(Vec2d(x2, y2), 180, 270, radius.toInt(), color2)
//        if(!excludedSectorSides.contains(ShadowRectSectorSides.LeftBottom)) Render2DUtilKt.drawSector2(Vec2d(x1, y2), 270, 360, radius.toInt(), color2)
    }
}
