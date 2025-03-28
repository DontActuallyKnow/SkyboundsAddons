package com.tmiq.ui

import com.tmiq.ui.components.UIComponent

class UIManager {

    private val components = mutableListOf<UIComponent>()

    fun add(component: UIComponent) {
        components.add(component)
    }

    fun draw(vg: Long) {
        components.forEach { it.draw(vg) }
    }

    fun handleMouseClick(x: Float, y: Float) {
        components.forEach { it.onMouseClick(x, y) }
    }

    fun handleKeyPress(key: Int) {
        components.forEach { it.onKeyPress(key) }
    }

    fun handleMouseMove(mouseX: Float, mouseY: Float) {
        components.forEach { it.onMouseMove(mouseX, mouseY) }
    }

    fun handleMouseRelease(mouseX: Float, mouseY: Float) {
        components.forEach { it.onMouseRelease(mouseX, mouseY) }
    }

    /**
     * Clears all components from the manager.
     * Useful when rebuilding the entire UI.
     */
    fun clear() {
        components.clear()
    }
}