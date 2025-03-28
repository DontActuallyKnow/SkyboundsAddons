package com.tmiq.ui.components

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class UIContainer(
    x: Float, y: Float, width: Float, height: Float,
    private val padding: Float = 20f
) : UIComponent(x, y, width, height) {

    private val children = mutableListOf<UIComponent>()

    val innerX: Float get() = x + padding
    val innerY: Float get() = y + padding
    val innerWidth: Float get() = width - (padding * 2)
    val innerHeight: Float get() = height - (padding * 2)

    fun add(component: UIComponent) {
        children.add(component)
    }

    fun clear() {
        children.clear()
    }

    override fun draw(vg: Long) {
        // Draw container background if needed
        // ...

        // Draw all children
        for (child in children) {
            child.draw(vg)
        }
    }

    override fun onMouseClick(mx: Float, my: Float) {
        for (child in children) {
            if (child.isPointInside(mx, my)) {
                child.onMouseClick(mx, my)
            }
        }
    }

    override fun onMouseMove(mx: Float, my: Float) {
        for (child in children) {
            child.onMouseMove(mx, my)
        }
    }

    override fun onMouseRelease(mx: Float, my: Float) {
        for (child in children) {
            child.onMouseRelease(mx, my)
        }
    }

    override fun onKeyPress(key: Int) {
        for (child in children) {
            child.onKeyPress(key)
        }
    }

}