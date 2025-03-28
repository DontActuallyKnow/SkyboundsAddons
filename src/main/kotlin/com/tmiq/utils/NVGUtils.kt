package com.tmiq.utils

import com.tmiq.ui.NanoVGRenderer
import org.lwjgl.nanovg.*
import org.lwjgl.system.MemoryStack
import java.awt.Color

object NVGUtils {

    val WHITE = Color(255, 255, 255, 255).rgb
    val BLACK = Color(0, 0, 0, 255).rgb
    val TRANSPARENT = Color(0, 0, 0, 0).rgb
    val PRIMARY = Color(33, 150, 243, 255).rgb
    val SECONDARY = Color(156, 39, 176, 255).rgb
    val SUCCESS = Color(76, 175, 80, 255).rgb
    val DANGER = Color(244, 67, 54, 255).rgb
    val WARNING = Color(255, 152, 0, 255).rgb
    val INFO = Color(33, 150, 243, 255).rgb

    fun getPixelRatio(): Float = 1f
    
    /**
     * Creates a 32-bit integer representing a color with the specified red, green, blue, and alpha values.
     *
     * @param r The red component of the color, ranging from 0 to 255.
     * @param g The green component of the color, ranging from 0 to 255.
     * @param b The blue component of the color, ranging from 0 to 255.
     * @param a The alpha (transparency) component of the color, ranging from 0 (completely transparent) to 255 (completely opaque).
     * @return A 32-bit integer representing the RGBA color.
     */
    fun rgba(r: Int, g: Int, b: Int, a: Int): Int {
        return Color(r, g, b, a).rgb
    }

    /**
     * Combines the red, green, and blue components into a single RGB integer value.
     *
     * @param r The red component of the color, in the range 0-255.
     * @param g The green component of the color, in the range 0-255.
     * @param b The blue component of the*/
    fun rgb(r: Int, g: Int, b: Int): Int {
        return Color(r, g, b).rgb
    }

    /**
     * Modifies the alpha component of a given color.
     *
     * @param color The original color as an integer.
     * @param alpha The desired alpha value, ranging from 0 to 255.
     * @return A new color integer with the specified alpha applied.
     */
    fun withAlpha(color: Int, alpha: Int): Int {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return Color(r, g, b, alpha).rgb
    }

    /**
     *
     */
    fun nvgColor(stack: MemoryStack, color: Int): NVGColor {
        val nvgColor = NVGColor.calloc(stack)
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val a = ((color shr 24) and 0xFF) / 255f
        NanoVG.nvgRGBAf(r, g, b, a, nvgColor)
        return nvgColor
    }

    fun drawRoundedRect(vg: Long, x: Float, y: Float, width: Float, height: Float,
                        radius: Float, color: Int, filled: Boolean = true, strokeWidth: Float = 1f) {
        MemoryStack.stackPush().use { stack ->
            val nvgColor = nvgColor(stack, color)

            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgRoundedRect(vg, x, y, width, height, radius)

            if (filled) {
                NanoVG.nvgFillColor(vg, nvgColor)
                NanoVG.nvgFill(vg)
            } else {
                NanoVG.nvgStrokeColor(vg, nvgColor)
                NanoVG.nvgStrokeWidth(vg, strokeWidth)
                NanoVG.nvgStroke(vg)
            }
        }
    }

    fun drawShadowRect(vg: Long, x: Float, y: Float, width: Float, height: Float,
                       radius: Float, shadowSize: Float, shadowColor: Int) {
        MemoryStack.stackPush().use { stack ->
            val shadowColorInner = nvgColor(stack, shadowColor)
            val shadowColorOuter = nvgColor(stack, withAlpha(shadowColor, 0))

            val shadowPaint = NVGPaint.calloc(stack)
            NanoVG.nvgBoxGradient(
                vg, x, y + 2, width, height,
                radius * 1.5f, shadowSize,
                shadowColorInner, shadowColorOuter, shadowPaint
            )

            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgRect(vg, x - shadowSize, y - shadowSize/2,
                width + shadowSize*2, height + shadowSize*2)
            NanoVG.nvgFillPaint(vg, shadowPaint)
            NanoVG.nvgFill(vg)
        }
    }

    fun drawText(vg: Long, text: String, x: Float, y: Float, size: Float,
                 color: Int, align: TextAlign = TextAlign.LEFT, fontName: String = "sans") {
        MemoryStack.stackPush().use { stack ->
            val nvgColor = nvgColor(stack, color)

            NanoVG.nvgFontSize(vg, size)
            NanoVG.nvgFontFace(vg, fontName)

            val alignValue = when(align) {
                TextAlign.LEFT -> NanoVG.NVG_ALIGN_LEFT
                TextAlign.CENTER -> NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_MIDDLE
                TextAlign.RIGHT -> NanoVG.NVG_ALIGN_RIGHT
            }
            NanoVG.nvgTextAlign(vg, alignValue)

            NanoVG.nvgFillColor(vg, nvgColor)
            NanoVG.nvgText(vg, x, y, text)
        }
    }

    fun measureTextWidth(vg: Long, text: String, size: Float, fontName: String = "sans"): Float {
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgFontFace(vg, fontName)

        MemoryStack.stackPush().use { stack ->
            val bounds = stack.mallocFloat(4)
            return NanoVG.nvgTextBounds(vg, 0f, 0f, text, bounds)
        }
    }

    fun drawGradientRect(vg: Long, x: Float, y: Float, width: Float, height: Float,
                         radius: Float, startColor: Int, endColor: Int, vertical: Boolean = false) {
        MemoryStack.stackPush().use { stack ->
            val startNvgColor = nvgColor(stack, startColor)
            val endNvgColor = nvgColor(stack, endColor)

            val gradientPaint = NVGPaint.calloc(stack)
            if (vertical) {
                NanoVG.nvgLinearGradient(
                    vg, x, y, x, y + height,
                    startNvgColor, endNvgColor, gradientPaint
                )
            } else {
                NanoVG.nvgLinearGradient(
                    vg, x, y, x + width, y,
                    startNvgColor, endNvgColor, gradientPaint
                )
            }

            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgRoundedRect(vg, x, y, width, height, radius)
            NanoVG.nvgFillPaint(vg, gradientPaint)
            NanoVG.nvgFill(vg)
        }
    }

    fun drawCircle(vg: Long, x: Float, y: Float, radius: Float,
                   color: Int, filled: Boolean = true, strokeWidth: Float = 1f) {
        MemoryStack.stackPush().use { stack ->
            val nvgColor = nvgColor(stack, color)

            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgCircle(vg, x, y, radius)

            if (filled) {
                NanoVG.nvgFillColor(vg, nvgColor)
                NanoVG.nvgFill(vg)
            } else {
                NanoVG.nvgStrokeColor(vg, nvgColor)
                NanoVG.nvgStrokeWidth(vg, strokeWidth)
                NanoVG.nvgStroke(vg)
            }
        }
    }

    fun drawImage(vg: Long, image: Int, x: Float, y: Float, width: Float, height: Float) {
        MemoryStack.stackPush().use { stack ->
            val paint = NVGPaint.calloc(stack)
            NanoVG.nvgImagePattern(vg, x, y, width, height, 0f, image, 1f, paint)

            NanoVG.nvgBeginPath(vg)
            NanoVG.nvgRect(vg, x, y, width, height)
            NanoVG.nvgFillPaint(vg, paint)
            NanoVG.nvgFill(vg)
        }
    }

    enum class TextAlign {
        LEFT, CENTER, RIGHT
    }
}