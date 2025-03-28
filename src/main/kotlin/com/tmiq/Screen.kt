package com.tmiq

import com.tmiq.ui.NanoVGRenderer
import com.tmiq.ui.components.UIButton
import com.tmiq.ui.components.UICheckbox
import com.tmiq.ui.components.UITextInput
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class Screen : Screen(Text.of("UI Test")) {

    override fun init() {
        super.init()

        NanoVGRenderer.init()

        val uiManager = NanoVGRenderer.getUiManager()
        uiManager.add(UIButton(50f, 50f, 120f, 40f, "Click Me") { println("Button clicked!") })
        uiManager.add(UICheckbox(50f, 100f, 20f) { println("Checkbox: $it") })
        uiManager.add(UITextInput(50f, 130f, 200f, 30f))
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(context, mouseX, mouseY, delta)

        if (context != null) {
            NanoVGRenderer.render(context, this.width, this.height)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        NanoVGRenderer.handleMouseClick(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        NanoVGRenderer.handleKeyPress(chr.code)
        return super.charTyped(chr, modifiers)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        NanoVGRenderer.handleKeyPress(keyCode)
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun removed() {
        super.removed()

        NanoVGRenderer.dispose()
    }

}