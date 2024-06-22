package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats

object FilledRenderLayer : RenderLayer(
    "filled_render_layer",
    VertexFormats.POSITION_COLOR,
    VertexFormat.DrawMode.TRIANGLE_STRIP,
    DEFAULT_BUFFER_SIZE,
    false,
    true,
    {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)

        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.pushMatrix()
        matrixStack.scale(0.99975586f, 0.99975586f, 0.99975586f)
        RenderSystem.applyModelViewMatrix()

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

    },
    {
        val matrixStack = RenderSystem.getModelViewStack()
        matrixStack.popMatrix()
        RenderSystem.applyModelViewMatrix()

        RenderSystem.disableBlend()
    }
)