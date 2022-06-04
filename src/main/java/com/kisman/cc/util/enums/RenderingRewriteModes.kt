package com.kisman.cc.util.enums

import com.kisman.cc.util.Rendering

enum class RenderingRewriteModes(
    val mode : Rendering.Mode
) {
    Filled(Rendering.Mode.BOX),
    Outline(Rendering.Mode.OUTLINE),
    Both(Rendering.Mode.BOTH),
    FilledGradient(Rendering.Mode.GRADIENT),
    OutlineGradient(Rendering.Mode.OUTLINE_GRADIENT),
    BothGradient(Rendering.Mode.BOTH_GRADIENT),
    GlowOutline(Rendering.Mode.CUSTOM_OUTLINE),
    Glow(Rendering.Mode.GRADIENT_CUSTOM_OUTLINE),
}