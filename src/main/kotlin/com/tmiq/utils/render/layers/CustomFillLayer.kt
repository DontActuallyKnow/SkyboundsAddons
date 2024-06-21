package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
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
        //RenderSystem.defaultBlendFunc()
        RenderSystem.blendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA)
        RenderSystem.enableDepthTest()
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        RenderSystem.depthFunc(GL30.GL_ALWAYS)
    },
    {
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
        RenderSystem.depthFunc(GL30.GL_LEQUAL)
    }
)