package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL30

object CustomLinesLayer : RenderLayer(
    "see_through_lines",
    VertexFormats.LINES,
    VertexFormat.DrawMode.LINES,
    256,
    false,
    true,
    {
        RenderSystem.lineWidth(3f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.depthMask(false)
        RenderSystem.depthFunc(GL30.GL_ALWAYS)
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)
    },
    {
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
        RenderSystem.depthMask(true)
        RenderSystem.depthFunc(GL30.GL_LEQUAL)
        RenderSystem.lineWidth(1f)
    }
)