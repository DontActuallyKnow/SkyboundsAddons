package com.tmiq.utils.hud

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import org.lwjgl.glfw.GLFW
class TestElement : HudRenderCallback {

    private var currentRenderPassMousePosition: Pair<Int, Int>? = null
    override fun onHudRender(context: DrawContext?, tickDelta: Float) {

        val x = 100.0
        val y = 200.0

        val modelMatrices = context?.matrices ?: return

        val renderer: TextRenderer = MinecraftClient.getInstance().textRenderer

        RenderSystem.enableBlend()

        RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE)

        modelMatrices.push()
        modelMatrices.scale(1.0f, 1.0f, 1.0f)
        modelMatrices.translate(x/2, y/2, 100.0)

        RenderSystem.applyModelViewMatrix()

        val mouseX = DoubleArray(1)
        val mouseY = DoubleArray(1)
        GLFW.glfwGetCursorPos(MinecraftClient.getInstance().window.handle, mouseX, mouseY)

        currentRenderPassMousePosition = Pair(mouseX[0].toInt(), mouseY[0].toInt())

        var text = "Hello, World! (${mouseX[0].toInt()}, ${mouseY[0].toInt()})"

        if (isHovered(x, y, renderer.getWidth(text))) {
            text = "§nHello, World! (${mouseX[0].toInt()}, ${mouseY[0].toInt()})"
        }

        context.drawText(renderer, text, 0, 0, 0xFFFFFF, true)

        RenderSystem.disableBlend()

        modelMatrices.pop()



    }
    fun isHovered(posX: Double, posY: Double, width: Int) = currentRenderPassMousePosition?.let { (x, y) ->
        x.toDouble() in (posX..posX + width) && y.toDouble() in (posY..posY + 10)
    } ?: false


}

