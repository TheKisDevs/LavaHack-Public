package com.kisman.cc.util.render.objects

import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.enums.Object3dTypes
import com.kisman.cc.util.render.konas.TessellatorUtil
import net.minecraft.client.renderer.GlStateManager

class BoxObject(val box: Box , override val color: Colour, val mode: BoxRenderModes, val width: Float, val depth: Boolean, val alpha: Boolean) : Abstract3dObject() {
    override val type: Object3dTypes = Object3dTypes.Box

    override fun draw(ticks: Float) {
        GlStateManager.pushMatrix()
        prepare(depth,alpha)

        if(mode != BoxRenderModes.Outline) TessellatorUtil.drawBox(box.toAABB(), color);
        if(mode != BoxRenderModes.Filled) TessellatorUtil.drawBoundingBox(box.toAABB(), width.toDouble(), color.withAlpha(255));

        GlStateManager.color(1F, 1F, 1F, 1F);

        release(alpha)
        GlStateManager.popMatrix()
    }
}