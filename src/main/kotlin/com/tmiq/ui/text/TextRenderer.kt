package com.tmiq.ui.text

import com.tmiq.ui.NanoVGRenderer
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG
import java.awt.Color

class TextRenderer(
    private val fontManager: FontManager
) {

    companion object {
        const val ALIGN_LEFT = 1
        const val ALIGN_CENTER = 2
        const val ALIGN_RIGHT = 4 // I don't know why i did this but i dont wanna change it
    }

    /**
     * A reusable instance of `NVGColor` used for color manipulations during rendering operations.
     *
     * This object is allocated once to avoid unnecessary allocations and improve performance
     * during frequent rendering tasks. Typically used to set color properties for NanoVG rendering
     * contexts such*/
    private val nvgColor = NVGColor.calloc()

    /**
     * Draws a text string at the specified position with the given styling attributes.
     *
     * @param text The text string to be drawn.
     * @param x The x*/
    fun drawText(
        text: String,
        x: Float,
        y: Float,
        fontSize: Float = 14f,
        color: Color = Color.WHITE,
        fontName: String = "regular",
        alignment: Int = ALIGN_LEFT
    ) {
        val vg = NanoVGRenderer.getVg()
        if (vg == 0L) return



        fontManager.getFont(fontName)?.let { fontId ->
            NanoVG.nvgFontFaceId(vg, fontId)
        } ?: NanoVG.nvgFontFace(vg, fontName)

        NanoVG.nvgFontSize(vg, fontSize)

        NanoVG.nvgTextAlign(vg, alignment)

        nvgColor.r(color.red / 255f)
            .g(color.green / 255f)
            .b(color.blue / 255f)
            .a(color.alpha / 255f)
        NanoVG.nvgFillColor(vg, nvgColor)

        NanoVG.nvgText(vg, x, y, text)
    }

    /**
     * Renders outlined text with the specified parameters.
     *
     * @param text The text to render.
     * @param x The x-coordinate for the position of the text.
     * @param y The y-coordinate for the baseline position of the text.
     * @param fontSize The size of the font. Default is 14f.
     * @param fillColor The color of the text fill. Default is Color.WHITE.
     * @param outlineColor The color of the text outline. Default is Color.BLACK.
     * @param outlineWidth The width of the text outline. Default is 1.5f.
     * @param fontName The name of the font*/
    fun drawTextOutlined(
        text: String,
        x: Float,
        y: Float,
        fontSize: Float = 14f,
        fillColor: Color = Color.WHITE,
        outlineColor: Color = Color.BLACK,
        outlineWidth: Float = 1.5f,
        fontName: String = "regular",
        alignment: Int = ALIGN_LEFT
    ) {
        val vg = NanoVGRenderer.getVg()
        if (vg == 0L) return



        fontManager.getFont(fontName)?.let { fontId ->
            NanoVG.nvgFontFaceId(vg, fontId)
        } ?: NanoVG.nvgFontFace(vg, fontName)

        NanoVG.nvgFontSize(vg, fontSize)
        NanoVG.nvgTextAlign(vg, alignment)

        nvgColor.r(outlineColor.red / 255f)
            .g(outlineColor.green / 255f)
            .b(outlineColor.blue / 255f)
            .a(outlineColor.alpha / 255f)
        NanoVG.nvgStrokeColor(vg, nvgColor)
        NanoVG.nvgStrokeWidth(vg, outlineWidth)
        NanoVG.nvgText(vg, x, y, text)

        nvgColor.r(fillColor.red / 255f)
            .g(fillColor.green / 255f)
            .b(fillColor.blue / 255f)
            .a(fillColor.alpha / 255f)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgText(vg, x, y, text)
    }

    /**
     * Measures the width of the given text based on the specified font settings.
     *
     * @param text The text whose width is to be measured.
     * @param fontSize The size of the font. Default is 14f.
     * @param fontName The name of the font to be used. Default is "regular".
     * @return The width of the text as a Float.
     */
    fun measureTextWidth(text: String, fontSize: Float = 14f, fontName: String = "regular"): Float {
        val vg = NanoVGRenderer.getVg()
        if (vg == 0L) return 0f



        fontManager.getFont(fontName)?.let { fontId ->
            NanoVG.nvgFontFaceId(vg, fontId)
        } ?: NanoVG.nvgFontFace(vg, fontName)

        NanoVG.nvgFontSize(vg, fontSize)

        return NanoVG.nvgTextBounds(vg, 0f, 0f, text, null as FloatArray?)
    }

    /**
     * Draws a block of text within a specified rectangular area.
     *
     * @param text The text to be drawn.
     * @param x The x-coordinate of the top-left corner of the text box.
     * @param y The y-coordinate of the top-left corner of the text box.
     */
    fun drawTextBox(
        text: String,
        x: Float,
        y: Float,
        width: Float,
        fontSize: Float = 14f,
        color: Color = Color.WHITE,
        fontName: String = "regular",
        alignment: Int = ALIGN_LEFT,
        lineHeight: Float = 1.2f
    ) {
        val vg = NanoVGRenderer.getVg()
        if (vg == 0L) return



        fontManager.getFont(fontName)?.let { fontId ->
            NanoVG.nvgFontFaceId(vg, fontId)
        } ?: NanoVG.nvgFontFace(vg, fontName)

        NanoVG.nvgFontSize(vg, fontSize)

        NanoVG.nvgTextAlign(vg, alignment)

        nvgColor.r(color.red / 255f)
            .g(color.green / 255f)
            .b(color.blue / 255f)
            .a(color.alpha / 255f)
        NanoVG.nvgFillColor(vg, nvgColor)

        NanoVG.nvgTextBox(vg, x, y, width, text)
    }

    /**
     * Releases the resources associated with the object.
     *
     * The method is responsible for freeing allocated resources to prevent
     * memory leaks. It ensures that the `nvgColor` object is released and
     * made available for garbage collection when it is no longer needed.
     *
     * Calling this method is necessary after the `nvgColor` object is used
     * to ensure proper resource management within the application.
     */
    fun dispose() {
        nvgColor.free()
    }

}