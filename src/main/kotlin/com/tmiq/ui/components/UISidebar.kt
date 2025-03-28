package com.tmiq.ui.components

import com.tmiq.ui.StyleManager
import com.tmiq.utils.NVGUtils

class UISidebar(
    x: Float, y: Float, width: Float, height: Float
) : UIComponent(x, y, width, height) {

    private val menuItems = mutableListOf<MenuItem>()
    private var activePageIndex = 0
    private var onPageChangeListener: ((Int) -> Unit)? = null

    data class MenuItem(val label: String, val icon: String = "")

    fun addMenuItem(label: String, icon: String = "") {
        menuItems.add(MenuItem(label, icon))
    }

    fun setOnPageChangeListener(listener: (Int) -> Unit) {
        onPageChangeListener = listener
    }

    override fun draw(vg: Long) {
        NVGUtils.drawRoundedRect(
            vg,
            x,
            y,
            width,
            height,
            1f,
            StyleManager.getStyle().background.dark.getBackgroundColor()
        )

        val itemHeight = 40f

        for (i in menuItems.indices) {
            val isActive = i == activePageIndex

            if (isActive) {
                NVGUtils.drawRoundedRect(
                    vg,
                    x,
                    y,
                    width,
                    height,
                    0f,
                    StyleManager.getStyle().background.dark.getForegroundColor()
                )
            }
        }
    }

    override fun onMouseClick(mx: Float, my: Float) {
        val itemHeight = 40f

        for (i in menuItems.indices) {
            val itemY = y + (i * itemHeight)

            if (mx >= x && mx <= x + width && my >= itemY && my <= itemY + itemHeight) {
                activePageIndex = i
                onPageChangeListener?.invoke(i)
                break
            }
        }
    }
}
