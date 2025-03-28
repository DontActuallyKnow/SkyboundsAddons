package com.tmiq.ui.components

import com.tmiq.utils.NVGUtils

class UITextInput(
    x: Float, y: Float, width: Float, height: Float,
    var text: String = "",
    val placeholder: String = "Enter text..."
) : UIComponent(x, y, width, height) {

    var focused = false

    override fun draw(vg: Long) {
        // Draw text input background
        val bgColor = if (focused) NVGUtils.rgb(30, 30, 30) else NVGUtils.rgb(20, 20, 20)
        NVGUtils.drawRoundedRect(vg, x, y, width, height, 3f, bgColor)

        // Draw border if focused
        if (focused) {
            NVGUtils.drawRoundedRect(
                vg,
                x,
                y,
                width,
                height,
                3f,
                NVGUtils.PRIMARY,
                false,
                1.5f
            )
        }

        // Draw text or placeholder
        val displayText = if (text.isEmpty()) placeholder else text
        val textColor = if (text.isEmpty()) NVGUtils.withAlpha(NVGUtils.WHITE, 120) else NVGUtils.rgb(200, 200, 200)

        NVGUtils.drawText(
            vg,
            displayText,
            x + 8,
            y + height / 2,
            16f,
            textColor,
            NVGUtils.TextAlign.LEFT
        )
    }

    override fun onMouseClick(mx: Float, my: Float) {
        focused = mx in x..(x + width) && my in y..(y + height)
    }

    override fun onKeyPress(key: Int) {
        if (focused) {
            if (key == 259 && text.isNotEmpty()) {
                text = text.dropLast(1)
            } else if (key in 32..126) {
                text += key.toChar()
            }
        }
    }
}