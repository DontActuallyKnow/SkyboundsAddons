package com.tmiq

import com.tmiq.ui.NanoVGRenderer
import com.tmiq.ui.components.*
import com.tmiq.ui.text.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.awt.Color

class Screen : Screen(Text.of("UI Test")) {

    private val uiManager = NanoVGRenderer.getUiManager()
    private lateinit var container: UIContainer
    private lateinit var splitLayout: UISplitLayout
    private lateinit var sidebar: UISidebar
    private lateinit var pageComponent: UIPage

    override fun init() {
        super.init()
        NanoVGRenderer.init()

        // Create main container with even padding
        container = UIContainer(
            x = width * 0.1f,  // 10% padding on left and right
            y = height * 0.1f, // 10% padding on top and bottom
            width = width * 0.8f,
            height = height * 0.8f,
            padding = 20f
        )

        // Create split layout with sidebar and content area
        splitLayout = UISplitLayout(
            container.innerX,
            container.innerY,
            container.innerWidth,
            container.innerHeight,
            sidebarWidth = 200f
        )

        // Create sidebar with menu items
        sidebar = UISidebar(
            splitLayout.sidebarX,
            splitLayout.sidebarY,
            200f,
            splitLayout.height
        )
        sidebar.addMenuItem("Dashboard")
        sidebar.addMenuItem("Settings")
        sidebar.addMenuItem("Profile")

        // Create page component
        pageComponent = UIPage(
            splitLayout.pageX,
            splitLayout.pageY,
            splitLayout.pageWidth,
            splitLayout.height
        )

        // Add components to first page (Dashboard)
        pageComponent.addComponentToPage(
            0,
            UILabel(
                0f, 0f, 300f, 40f, "Dashboard",
                fontSize = 24f, fontName = "bold",
                textColor = Color.WHITE, alignment = TextRenderer.ALIGN_CENTER
            )
        )
        pageComponent.addComponentToPage(
            0,
            UIButton(0f, 0f, 200f, 40f, "Dashboard Action", {
                println("Dashboard action clicked!")
            }, UIButton.ButtonStyle.PRIMARY)
        )

        // Add components to second page (Settings)
        pageComponent.addComponentToPage(
            1,
            UILabel(
                0f, 0f, 300f, 40f, "Settings",
                fontSize = 24f, fontName = "bold",
                textColor = Color.WHITE, alignment = TextRenderer.ALIGN_CENTER
            )
        )
        pageComponent.addComponentToPage(
            1,
            UISwitch(0f, 0f, 60f, 20f, false) {
                println("Settings switch toggled to $it")
            }
        )

        pageComponent.addComponentToPage(
            2,
            UILabel(
                0f, 0f, 300f, 40f, "Profile",
                fontSize = 24f, fontName = "bold",
                textColor = Color.WHITE, alignment = TextRenderer.ALIGN_CENTER
            )
        )
        pageComponent.addComponentToPage(
            2,
            UIButton(0f, 0f, 200f, 40f, "Edit Profile", {
                println("Edit profile clicked!")
            }, UIButton.ButtonStyle.SECONDARY)
        )

        // Link sidebar page changes to the pageComponent
        sidebar.setOnPageChangeListener { pageIndex ->
            pageComponent.setPage(pageIndex)
        }

        // Connect the components
        splitLayout.setSidebar(sidebar)
        splitLayout.setPageComponent(pageComponent)
        container.add(splitLayout)

        // Add the container to the UI manager
        uiManager.add(container)
    }


    override fun shouldPause(): Boolean {
        return false
    }

    override fun renderBackground(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {

    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)

        if (context != null) {
            NanoVGRenderer.render(context, this.width, this.height)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        NanoVGRenderer.handleMouseClick(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        NanoVGRenderer.handleKeyPress(keyCode)
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        NanoVGRenderer.handleMouseMove(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        NanoVGRenderer.handleMouseRelease(mouseX.toFloat(), mouseY.toFloat())
        return super.mouseReleased(mouseX, mouseY, button)
    }


    override fun removed() {
        super.removed()

        uiManager.clear()
        NanoVGRenderer.dispose()
    }

}