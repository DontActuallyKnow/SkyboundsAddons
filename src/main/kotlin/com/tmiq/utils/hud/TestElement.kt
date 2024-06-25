package com.tmiq.utils.hud

import com.mojang.blaze3d.systems.RenderSystem
import com.tmiq.utils.KeyboardUtils.isKeyClicked
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.glfw.GLFWMouseButtonCallbackI

class TestElement : HudRenderCallback {

    private var currentRenderPassMousePosition: Pair<Int, Int>? = null
    private var started = false

    class MouseButtonCallback : GLFWMouseButtonCallback() {
        override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
            MinecraftClient.getInstance().player?.sendMessage(Text.of("Button: $button, Action: $action, Mods: $mods"), false)
        }

    }
    override fun onHudRender(context: DrawContext?, tickDelta: Float) {
        if (!started) {
            // Tracks any mouse button clicks (Button 0 = Left click, Button 1 = Right click)
            // Action 1 = Pressed, Action 0 = Released (use Action 1 to track when something is clicked)
            // Unsure what mods is
            GLFW.glfwSetMouseButtonCallback(MinecraftClient.getInstance().window.handle, MouseButtonCallback())
            started = true
        }
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
            text = "§nHello, World! (${mouseX[0].toInt()}, ${mouseY[0].toInt()}) ()"
            if (GLFW.GLFW_MOUSE_BUTTON_LEFT.isKeyClicked()) {
                MinecraftClient.getInstance().player?.sendMessage(Text.of("§aHello, World! (${mouseX[0].toInt()}, ${mouseY[0].toInt()})"), false)
            }

        }

        context.drawText(renderer, text, 0, 0, 0xFFFFFF, true)

        RenderSystem.disableBlend()

        modelMatrices.pop()



    }
    fun isHovered(posX: Double, posY: Double, width: Int) = currentRenderPassMousePosition?.let { (x, y) ->
        x.toDouble() in (posX..posX + width) && y.toDouble() in (posY..posY + 10)
    } ?: false


}

