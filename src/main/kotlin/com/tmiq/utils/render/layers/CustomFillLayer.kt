package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL30

object CustomFillLayer : RenderLayer(
    "see_through_fill",
    VertexFormats.POSITION_COLOR,
    VertexFormat.DrawMode.TRIANGLE_STRIP,
    1536,
    false,
    true,
    {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        //RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.depthMask(false)
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        RenderSystem.depthFunc(GL30.GL_ALWAYS)
    },
    {
        RenderSystem.enableDepthTest()
        //RenderSystem.enableCull()
        RenderSystem.disableBlend()
        RenderSystem.depthMask(true)
        RenderSystem.depthFunc(GL30.GL_LEQUAL)
    }
)