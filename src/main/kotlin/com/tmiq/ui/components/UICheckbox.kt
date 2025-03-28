package com.tmiq.ui.components

import com.tmiq.utils.NVGUtils

class UICheckbox(
    x: Float, y: Float, val size: Float,
    var checked: Boolean = false,
    val onToggle: (Boolean) -> Unit
) : UIComponent(x, y, size, size) {

    override fun draw(vg: Long) {
        NVGUtils.drawRoundedRect(vg, x, y, size, size, 2f, NVGUtils.rgb(50, 50, 50))

        if (checked) {
            NVGUtils.drawRoundedRect(
                vg,
                x + 4,
                y + 4,
                size - 8,
                size - 8,
                1f,
                NVGUtils.rgb(0, 255, 0)
            )
        }
    }

    override fun onMouseClick(mx: Float, my: Float) {
        if (mx in x..(x + size) && my in y..(y + size)) {
            checked = !checked
            onToggle(checked)
        }
    }
}