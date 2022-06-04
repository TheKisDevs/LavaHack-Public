package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.client.shader.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShaderGroup.class)
public interface AccessorShaderGroup {
    @Accessor(value = "listShaders") List<Shader> getListShaders();
    @Accessor(value = "listFramebuffers") List<Framebuffer> getListFramebuffers();
}
