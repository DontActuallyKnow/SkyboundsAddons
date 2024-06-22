package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

object FilledRenderLayer : RenderLayer(
    "filled_render_layer",
    VertexFormats.POSITION_COLOR,
    VertexFormat.DrawMode.TRIANGLE_STRIP,
    CUTOUT_BUFFER_SIZE,
    false,
    true,
    {

        RenderSystem.setShader(GameRenderer::getPositionColorProgram)

        //RenderPhase.POLYGON_OFFSET_LAYERING
        RenderSystem.polygonOffset(-1.0f, -10.0f)
        RenderSystem.enablePolygonOffset()

        //transparency(DEFAULT_TRANSPARENCY)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        RenderSystem.depthFunc(GL30.GL_LEQUAL)
    },
    {
        RenderSystem.polygonOffset(0.0f, 0.0f)
        RenderSystem.disablePolygonOffset()

        RenderSystem.disableBlend()

        RenderSystem.depthFunc(GL30.GL_LEQUAL)

    }
)