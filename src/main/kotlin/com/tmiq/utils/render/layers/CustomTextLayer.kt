package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL30

object CustomTextLayer : RenderLayer(
    "see_through_text",
    VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
    VertexFormat.DrawMode.QUADS,
    256,
    false,
    true,
    {
        RenderSystem.lineWidth(1.5f)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        //RenderSystem.depthMask(false)
        RenderSystem.setShader(GameRenderer::getPositionTexLightmapColorProgram)
        RenderSystem.depthFunc(GL30.GL_ALWAYS)
    },
    {
        RenderSystem.enableDepthTest()
        RenderSystem.enableCull()
        RenderSystem.disableBlend()
        //RenderSystem.depthMask(true)
        RenderSystem.depthFunc(GL30.GL_LEQUAL)
        RenderSystem.lineWidth(1f)
    }
)