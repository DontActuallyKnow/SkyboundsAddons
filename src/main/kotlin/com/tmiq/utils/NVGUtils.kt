package com.tmiq.utils

import net.minecraft.client.MinecraftClient
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.system.MemoryStack
import java.awt.Color

object NVGUtils {

    /**
     * Retrieves the pixel ratio (or scale factor) used by the Minecraft client window.
     *
     * @return The pixel ratio as a Float, which represents the scaling factor of the client window.
     */
    fun getPixelRatio(): Float {
        return MinecraftClient.getInstance().window.scaleFactor.toFloat()
    }

    fun save(vg: Long) {
        NanoVG.nvgSave(vg)
    }

    fun restore(vg: Long) {
        NanoVG.nvgRestore(vg)
    }

    fun scissor(vg: Long, x: Float, y: Float, width: Float, height: Float) {
        NanoVG.nvgScissor(vg, x, y, width, height)
    }

    fun resetScissor(vg: Long) {
        NanoVG.nvgResetScissor(vg)
    }

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
     * Creates a NanoVG color object from an ARGB integer color value.
     *
     * @param stack The memory stack used for allocating the NanoVG color object.
     * @param color The ARGB integer color value where each byte represents alpha, red, green, and blue channels respectively.
     * @return A NanoVG color object representing the specified ARGB color.
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

    /**
     * Draws a rounded rectangle on a NanoVG drawing context.
     *
     * This function supports both filled and stroked rectangles, allowing customization
     * of corner radius, color, and stroke width.
     *
     * @param vg         NanoVG drawing context handle.
     * @param x          X-coordinate of the top-left corner of the rectangle.
     * @param y          Y-coordinate of the top-left corner of the rectangle.
     * @param width      Width of the rectangle.
     * @param height     Height of the rectangle.
     * @param radius     Corner radius for the rounded rectangle.
     * @param color      Color of the rectangle in ARGB format.
     * @param filled     Determines whether the rectangle is filled. Default is true.
     * @param strokeWidth Width of the stroke line if `filled` is false. Default is 1f.
     */
    fun drawRoundedRect(
        vg: Long, x: Float, y: Float, width: Float, height: Float,
        radius: Float, color: Int, filled: Boolean = true, strokeWidth: Float = 1f
    ) {
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

    /**
     * Draws a rectangle with a shadow effect using a box gradient.
     *
     * @param vg The NanoVG context handle used for rendering.
     * @param x The x-coordinate of the rectangle.
     * @param y The y-coordinate of the rectangle.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param radius The corner radius of the rectangle.
     * @param shadowSize The size of the shadow effect.
     * @param shadowColor The color of the shadow as an integer.
     */
    fun drawShadowRect(
        vg: Long, x: Float, y: Float, width: Float, height: Float,
        radius: Float, shadowSize: Float, shadowColor: Int
    ) {
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
            NanoVG.nvgRect(
                vg, x - shadowSize, y - shadowSize / 2,
                width + shadowSize * 2, height + shadowSize * 2
            )
            NanoVG.nvgFillPaint(vg, shadowPaint)
            NanoVG.nvgFill(vg)
        }
    }

    // TODO Create draw text

    /**
     * Measures the width of a given text string rendered with a specified font size and font face.
     *
     * @param vg the NanoVG context reference.
     * @param text the string of text whose width is to be measured.
     * @param size the font size to use for the measurement.
     * @param fontName the name of the font to use for the measurement. Defaults to "sans".
     * @return a Float representing the width of the text in pixels.
     */
    fun measureTextWidth(vg: Long, text: String, size: Float, fontName: String = "sans"): Float {
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgFontFace(vg, fontName)

        MemoryStack.stackPush().use { stack ->
            val bounds = stack.mallocFloat(4)
            return NanoVG.nvgTextBounds(vg, 0f, 0f, text, bounds)
        }
    }

    /**
     * Draws a rectangle with a gradient fill. The gradient can be either vertical or horizontal,
     * transitioning between the specified start and end colors.
     *
     * @param vg The NanoVG context handle.
     * @param x The x-coordinate of the rectangle's top-left corner.
     * @param y The y-coordinate of the rectangle's top-left corner.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param radius The corner radius for rounding the rectangle. Set to 0 for sharp corners.
     * @param startColor The starting color of the gradient, in ARGB format.
     * @param endColor The ending color of the gradient, in ARGB format.
     * @param vertical A boolean indicating the direction of the gradient.
     *                 If true, the gradient is vertical (top-to-bottom). Defaults to false (left-to-right).
     */
    fun drawGradientRect(
        vg: Long, x: Float, y: Float, width: Float, height: Float,
        radius: Float, startColor: Int, endColor: Int, vertical: Boolean = false
    ) {
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

    /**
     * Draws a circle on the screen using the NanoVG graphics library.
     *
     * @param vg The handle to the NanoVG context.
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param radius The radius of the circle.
     * @param color The color of the circle in ARGB format.
     * @param filled Indicates whether the circle should be filled. If false, the circle will be stroked.
     * @param strokeWidth The width of the stroke when `filled` is false. Defaults to 1f.
     */
    fun drawCircle(
        vg: Long, x: Float, y: Float, radius: Float,
        color: Int, filled: Boolean = true, strokeWidth: Float = 1f
    ) {
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

    /**
     * Draws an image onto the specified context at a given position and size.
     *
     * @param vg The handle to the NanoVG context.
     * @param image The identifier of the image to be drawn.
     * @param x The x-coordinate of the top-left corner where the image will be drawn.
     * @param y The y-coordinate of the top-left corner where the image will be drawn.
     * @param width The width of the image.
     * @param height The height of the image.
     */
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