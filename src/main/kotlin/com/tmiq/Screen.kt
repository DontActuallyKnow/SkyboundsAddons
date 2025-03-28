package com.tmiq

import com.tmiq.ui.NanoVGRenderer
import com.tmiq.ui.StyleManager
import com.tmiq.ui.components.UIButton
import com.tmiq.ui.components.UILabel
import com.tmiq.ui.components.UISwitch
import com.tmiq.ui.text.TextRenderer
import com.tmiq.utils.UIUtils
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.awt.Color

class Screen : Screen(Text.of("UI Test")) {

    val uiManager = NanoVGRenderer.getUiManager()

    override fun init() {
        super.init()

        NanoVGRenderer.init()

        val label = UILabel(
            100f, 50f, 200f, 30f, "Hello World",
            fontSize = 20f, fontName = "bold", textColor = Color.WHITE, alignment = TextRenderer.ALIGN_CENTER
        )

        uiManager.add(UISwitch(50f, 1f, 60f, 20f, false) {
            println("Switch is toggled to $it")
            label.setText("The switch is toggled to $it")
        })

        uiManager.add(UIButton(50f, 50f, 120f, 40f, "Primary", {
            println("Primary button clicked!")
            label.setText("The primary button was clicked!")
            StyleManager.toggleDarkMode()
        }, UIButton.ButtonStyle.PRIMARY))

        // Secondary button
        uiManager.add(UIButton(50f, 100f, 120f, 40f, "Secondary", {
            println("Secondary button clicked!")
            label.setText("The secondary button was clicked!")
        }, UIButton.ButtonStyle.SECONDARY))

        // Success button
        uiManager.add(UIButton(50f, 150f, 120f, 40f, "Success", {
            println("Success button clicked!")
            label.setText("The success button was clicked!")
        }, UIButton.ButtonStyle.SUCCESS))

        // Danger button
        uiManager.add(UIButton(50f, 200f, 120f, 40f, "Danger", {
            println("Danger button clicked!")
            UIUtils.closeScreen()
        }, UIButton.ButtonStyle.DANGER))

        uiManager.add(label)
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun renderBackground(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {

    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)

        if (context != null) {
            NanoVGRenderer.render(context, this.width, this.height)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        NanoVGRenderer.handleMouseClick(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        NanoVGRenderer.handleKeyPress(keyCode)
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        NanoVGRenderer.handleMouseMove(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        NanoVGRenderer.handleMouseRelease(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseReleased(mouseX, mouseY, button)
    }


    override fun removed() {
        super.removed()

        uiManager.clear()
        NanoVGRenderer.dispose()
    }

}