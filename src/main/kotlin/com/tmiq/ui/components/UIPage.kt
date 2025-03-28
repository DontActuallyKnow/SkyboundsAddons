package com.tmiq.ui.components

import com.tmiq.ui.StyleManager
import com.tmiq.utils.NVGUtils

class UIPage(
    x: Float, y: Float, width: Float, height: Float
) : UIComponent(x, y, width, height) {

    private val pages = mutableMapOf<Int, MutableList<UIComponent>>()
    private var currentPage = 0

    fun addComponentToPage(pageIndex: Int, component: UIComponent) {
        if (!pages.containsKey(pageIndex)) {
            pages[pageIndex] = mutableListOf()
        }
        pages[pageIndex]?.add(component)

        // TODO Automatic layout can be implemented here
        arrangeComponentsOnPage(pageIndex)
    }

    fun setPage(pageIndex: Int) {
        currentPage = pageIndex
        // TODO trigger a layout refresh if needed
    }

    private fun arrangeComponentsOnPage(pageIndex: Int) {
        val components = pages[pageIndex] ?: return

        val padding = 20f
        var currentY = y + padding

        for (component in components) {
            component.x = x + (width - component.width) / 2
            component.y = currentY

            currentY += component.height + padding
        }
    }

    override fun draw(vg: Long) {
        // Draw page background
        NVGUtils.drawRoundedRect(
            vg,
            x,
            y,
            width,
            height,
            0f,
            StyleManager.getStyle().background.light.getBackgroundColor()
        )

        // Draw current page components
        pages[currentPage]?.forEach { it.draw(vg) }
    }

    override fun onMouseClick(mx: Float, my: Float) {
        pages[currentPage]?.forEach { component ->
            if (component.isPointInside(mx, my)) {
                component.onMouseClick(mx, my)
            }
        }
    }

    override fun onMouseMove(mx: Float, my: Float) {
        pages[currentPage]?.forEach { it.onMouseMove(mx, my) }
    }

    override fun onMouseRelease(mx: Float, my: Float) {
        pages[currentPage]?.forEach { it.onMouseRelease(mx, my) }
    }

    override fun onKeyPress(key: Int) {
        pages[currentPage]?.forEach { it.onKeyPress(key) }
    }


}