package com.tmiq.ui.components

class UISplitLayout(
    x: Float, y: Float, width: Float, height: Float,
    private val sidebarWidth: Float = 200f
) : UIComponent(x, y, width, height) {

    private var sidebar: UISidebar? = null
    private var pageComponent: UIPage? = null

    val sidebarX: Float get() = x
    val sidebarY: Float get() = y
    val pageX: Float get() = x + sidebarWidth
    val pageY: Float get() = y
    val pageWidth: Float get() = width - sidebarWidth

    fun setSidebar(sidebar: UISidebar) {
        this.sidebar = sidebar
        sidebar.x = sidebarX
        sidebar.y = sidebarY
        sidebar.width = sidebarWidth
        sidebar.height = height
    }

    fun setPageComponent(pageComponent: UIPage) {
        this.pageComponent = pageComponent
        pageComponent.x = pageX
        pageComponent.y = pageY
        pageComponent.width = pageWidth
        pageComponent.height = height
    }

    override fun draw(vg: Long) {
        sidebar?.draw(vg)
        pageComponent?.draw(vg)
    }

    override fun onMouseClick(mx: Float, my: Float) {
        if (sidebar != null && sidebar!!.isPointInside(mx, my)) {
            sidebar!!.onMouseClick(mx, my)
        } else if (pageComponent != null && pageComponent!!.isPointInside(mx, my)) {
            pageComponent!!.onMouseClick(mx, my)
        }
    }

    override fun onMouseMove(mx: Float, my: Float) {
        sidebar?.onMouseMove(mx, my)
        pageComponent?.onMouseMove(mx, my)
    }

    override fun onMouseRelease(mx: Float, my: Float) {
        sidebar?.onMouseRelease(mx, my)
        pageComponent?.onMouseRelease(mx, my)
    }

    override fun onKeyPress(key: Int) {
        sidebar?.onKeyPress(key)
        pageComponent?.onKeyPress(key)
    }

}