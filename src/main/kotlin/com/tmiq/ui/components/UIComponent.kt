package com.tmiq.ui.components

abstract class UIComponent(
    var x: Float, var y: Float, var width: Float, var height: Float
) {
    open fun draw(vg: Long) {}
    open fun onMouseClick(x: Float, y: Float) {}
    open fun onMouseMove(x: Float, y: Float) {}
    open fun onMouseRelease(x: Float, y: Float) {}
    open fun onKeyPress(key: Int) {}

    fun isPointInside(mx: Float, my: Float): Boolean {
        return mx >= x && mx <= x + width && my >= y && my <= y + height
    }
}