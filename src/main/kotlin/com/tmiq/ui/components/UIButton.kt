package com.tmiq.ui.components

import com.tmiq.utils.NVGUtils

class UIButton(
    x: Float, y: Float, width: Float, height: Float,
    var label: String, var onClick: () -> Unit
) : UIComponent(x, y, width, height) {

    override fun draw(vg: Long) {
        NVGUtils.drawRoundedRect(vg, x, y, width, height, 4f, NVGUtils.rgb(30, 30, 30))

        NVGUtils.drawText(
            vg,
            label,
            x + width / 2,
            y + height / 2,
            18f,
            NVGUtils.WHITE,
            NVGUtils.TextAlign.CENTER
        )
    }

    override fun onMouseClick(mx: Float, my: Float) {
        if (mx in x..(x + width) && my in y..(y + height)) {
            onClick()
        }
    }
}