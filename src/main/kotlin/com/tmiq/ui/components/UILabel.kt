package com.tmiq.ui.components

import com.tmiq.ui.NanoVGRenderer
import com.tmiq.ui.text.TextRenderer
import java.awt.Color

class UILabel(
    x: Float, y: Float, width: Float, height: Float,
    private var text: String,
    private var fontSize: Float = 12f,
    private var fontName: String = "regular",
    private var textColor: Color = Color.WHITE,
    private var alignment: Int = TextRenderer.ALIGN_LEFT
) : UIComponent(x, y, width, height) {

    fun setText(text: String) {
        this.text = text
    }

    fun setFontSize(fontSize: Float) {
        this.fontSize = fontSize
    }

    fun setFontName(fontName: String) {
        this.fontName = fontName
    }

    fun setFontColor(textColor: Color) {
        this.textColor = textColor
    }

    fun setAlignment(alignment: Int) {
        this.alignment = alignment
    }

    fun getText(): String {
        return text
    }

    override fun draw(vg: Long) {
        val textRenderer = NanoVGRenderer.getTextRenderer()

        if (width > 0) {
            textRenderer.drawTextBox(
                text,
                x,
                y,
                width,
                fontSize,
                textColor,
                fontName,
                alignment
            )
        } else {
            val textY = y + height / 2 + fontSize / 3
            val textX = when (alignment) {
                TextRenderer.ALIGN_CENTER -> x + width / 2
                TextRenderer.ALIGN_RIGHT -> x + width
                else -> x
            }

            textRenderer.drawText(
                text,
                textX,
                textY,
                fontSize,
                textColor,
                fontName,
                alignment
            )
        }
    }

}