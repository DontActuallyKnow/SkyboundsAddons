package com.tmiq.utils.render.layers

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats

object ESPLayer : RenderLayer(
    "esp",
    VertexFormats.POSITION_COLOR,
    VertexFormat.DrawMode.LINES,
    256,
    false,
    true,
    {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
    },
    {
        RenderSystem.disableBlend()
        RenderSystem.enableDepthTest()
    }
)