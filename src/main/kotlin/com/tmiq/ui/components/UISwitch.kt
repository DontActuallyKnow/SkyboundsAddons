package com.tmiq.ui.components

import com.tmiq.ui.StyleManager
import com.tmiq.utils.NVGUtils

class UISwitch(
    x: Float, y: Float, width: Float, height: Float,
    private var isOn: Boolean = false,
    var onChange: (Boolean) -> Unit = {}
) : UIComponent(x, y, width, height) {

    private var isHovered = false
    private var isClicked = false
    private var hoverProgress = 0f
    private var toggleProgress = if (isOn) 1f else 0f

    private var lastUpdateTimeNano = System.nanoTime()

    private fun easeInOut(t: Float): Float {
        return when {
            t < 0.5f -> 2f * t * t
            else -> -1f + (4f - 2f * t) * t
        }
    }

    override fun draw(vg: Long) {
        val currentTimeNano = System.nanoTime()
        val deltaMs = (currentTimeNano - lastUpdateTimeNano) / 1_000_000f
        lastUpdateTimeNano = currentTimeNano

        val animSettings = StyleManager.getStyle().animations

        hoverProgress = if (isHovered) {
            (hoverProgress + deltaMs / animSettings.hoverTransitionMs).coerceAtMost(1f)
        } else {
            (hoverProgress - deltaMs / animSettings.hoverTransitionMs).coerceAtLeast(0f)
        }

        val linearToggleProgress = if (isOn) {
            (toggleProgress + deltaMs / animSettings.clickTransitionMs).coerceAtMost(1f)
        } else {
            (toggleProgress - deltaMs / animSettings.clickTransitionMs).coerceAtLeast(0f)
        }
        toggleProgress = linearToggleProgress

        val easedToggleProgress = easeInOut(toggleProgress)

        val colorTheme = StyleManager.getStyle().primary
        val isDark = StyleManager.isDarkMode()
        val colorPair = if (isDark) colorTheme.dark else colorTheme.light

        val backgroundColor = if (isOn) {
            colorPair.getBackgroundColor()
        } else {
            NVGUtils.withAlpha(colorPair.getBackgroundColor(), 100)
        }

        val thumbColor = colorPair.getForegroundColor()

        val trackHeight = height * 0.8f
        val trackWidth = width
        val trackY = y + (height - trackHeight) / 2
        val cornerRadius = trackHeight / 2f

        val thumbSize = trackHeight * 0.8f
        val thumbPadding = (trackHeight - thumbSize) / 2
        val thumbTravel = trackWidth - thumbSize - thumbPadding * 2
        val thumbX = x + thumbPadding + (easedToggleProgress * thumbTravel)
        val thumbY = trackY + thumbPadding

        val baseAlpha = NVGUtils.withAlpha(backgroundColor, 255)
        NVGUtils.drawRoundedRect(vg, x, trackY, trackWidth, trackHeight, cornerRadius, baseAlpha, true)

        if (hoverProgress > 0) {
            val hoverAlpha = (40 * hoverProgress).toInt()
            val hoverColor = NVGUtils.withAlpha(0xFFFFFF, hoverAlpha)
            NVGUtils.drawRoundedRect(vg, x, trackY, trackWidth, trackHeight, cornerRadius, hoverColor, true)
        }

        val shadowOffset = 2f * hoverProgress
        NVGUtils.drawShadowRect(
            vg, thumbX - shadowOffset, thumbY - shadowOffset,
            thumbSize + shadowOffset * 2, thumbSize + shadowOffset * 2,
            thumbSize / 2, 4f, NVGUtils.withAlpha(0x000000, (60 * hoverProgress).toInt())
        )

        NVGUtils.drawRoundedRect(
            vg, thumbX, thumbY, thumbSize, thumbSize,
            thumbSize / 2, thumbColor, true
        )
    }

    override fun onMouseMove(mx: Float, my: Float) {
        isHovered = isPointInside(mx, my)
    }

    override fun onMouseClick(mx: Float, my: Float) {
        if (isPointInside(mx, my)) {
            isClicked = true
            toggle()
        }
    }

    override fun onMouseRelease(mx: Float, my: Float) {
        isClicked = false
    }

    private fun toggle() {
        isOn = !isOn
        onChange(isOn)
    }

    fun setState(on: Boolean) {
        if (isOn != on) {
            isOn = on
        }
    }

    fun getState(): Boolean {
        return isOn
    }

}