package com.tmiq.ui.components

import com.tmiq.ui.StyleManager
import com.tmiq.utils.NVGUtils

class UIButton(
    x: Float, y: Float, width: Float, height: Float,
    private var label: String, var onClick: () -> Unit,
    private var styleType: ButtonStyle = ButtonStyle.PRIMARY
) : UIComponent(x, y, width, height) {

    enum class ButtonStyle {
        PRIMARY, SECONDARY, TERTIARY, SUCCESS, WARNING, DANGER, INFO, OUTLINE
    }

    private var isHovered = false
    private var isClicked = false
    private var hoverProgress = 0f
    private var clickProgress = 0f

    private var lastUpdateTimeNano = System.nanoTime()

    override fun draw(vg: Long) {
        val currentTimeNano = System.nanoTime()
        val deltaTimeMs = (currentTimeNano - lastUpdateTimeNano) / 1_000_000f
        lastUpdateTimeNano = currentTimeNano

        val style = StyleManager.getStyle()
        val isDarkMode = StyleManager.isDarkMode()
        val animations = style.animations

        if (isHovered && hoverProgress < 1f || !isHovered && hoverProgress > 0f) {
            hoverProgress = if (isHovered) {
                (hoverProgress + deltaTimeMs / animations.hoverTransitionMs).coerceIn(0f, 1f)
            } else {
                (hoverProgress - deltaTimeMs / animations.hoverTransitionMs).coerceIn(0f, 1f)
            }
        }

        if (isClicked && clickProgress < 1f || !isClicked && clickProgress > 0f) {
            clickProgress = if (isClicked) {
                (clickProgress + deltaTimeMs / animations.clickTransitionMs).coerceIn(0f, 1f)
            } else {
                (clickProgress - deltaTimeMs / animations.clickTransitionMs).coerceIn(0f, 1f)
            }
        }

        val colorTheme = when (styleType) {
            ButtonStyle.PRIMARY -> style.primary
            ButtonStyle.SECONDARY -> style.secondary
            ButtonStyle.TERTIARY -> style.tertiary
            ButtonStyle.SUCCESS -> style.success
            ButtonStyle.WARNING -> style.warning
            ButtonStyle.DANGER -> style.danger
            ButtonStyle.INFO -> style.info
            ButtonStyle.OUTLINE -> style.primary // Assuming outline uses primary colors
        }

        val colorPair = if (isDarkMode) colorTheme.dark else colorTheme.light

        val backgroundColor = colorPair.getBackgroundColor()
        val textColor = colorPair.getForegroundColor()

        val scale = if (hoverProgress > 0f || clickProgress > 0f) {
            1f + (animations.buttonHoverScale - 1f) * hoverProgress -
                    (1f - animations.buttonClickScale) * clickProgress
        } else {
            1f
        }

        val scaledWidth = width * scale
        val scaledHeight = height * scale

        val offsetX = (width - scaledWidth) / 2
        val offsetY = (height - scaledHeight) / 2

        val baseAlpha = NVGUtils.withAlpha(backgroundColor, 255)
        NVGUtils.drawRoundedRect(vg, x + offsetX, y + offsetY, scaledWidth, scaledHeight, 2f, baseAlpha)

        if (hoverProgress > 0) {
            val hoverAlpha = (40 * hoverProgress).toInt()
            val hoverColor = NVGUtils.withAlpha(0xFFFFFF, hoverAlpha)
            NVGUtils.drawRoundedRect(vg, x + offsetX, y + offsetY, scaledWidth, scaledHeight, 2f, hoverColor)
        }

        val textX = x + width / 2
        val textY = y + height / 2
    }

    override fun onMouseMove(mx: Float, my: Float) {
        isHovered = mx >= x && mx <= x + width && my >= y && my <= y + height

        // Reset click state if mouse moved out
        if (!isHovered) {
            isClicked = false
        }
    }

    override fun onMouseClick(mx: Float, my: Float) {
        if (mx >= x && mx <= x + width && my >= y && my <= y + height) {
            isClicked = true
            onClick()
        }
    }

    override fun onMouseRelease(mx: Float, my: Float) {
        isClicked = false
    }
}
