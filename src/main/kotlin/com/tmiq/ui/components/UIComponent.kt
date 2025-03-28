package com.tmiq.ui.components

abstract class UIComponent(
    var x: Float, var y: Float, var width: Float, var height: Float
) {

    open fun draw(vg: Long) {}
    open fun onMouseClick(x: Float, y: Float) {}
    open fun onKeyPress(key: Int) {}

}