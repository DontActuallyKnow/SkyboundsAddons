package com.tmiq.ui

import com.mojang.blaze3d.systems.RenderSystem
import com.tmiq.ui.text.FontManager
import com.tmiq.ui.text.TextRenderer
import com.tmiq.utils.NVGUtils
import net.minecraft.client.gui.DrawContext
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL11

object NanoVGRenderer {

    private var vg: Long = 0
    private val uiManager = UIManager()

    private val fontManager = FontManager()
    private lateinit var textRenderer: TextRenderer

    /**
     * Retrieves the unique identifier for the vector graphics rendering context.
     *
     * @return the identifier (vg) as a Long used for rendering graphical components.
     */
    fun getVg(): Long {
        return vg
    }

    /**
     * Retrieves the instance of the UIManager for managing UI components and interactions.
     *
     * @return the UIManager instance managing UI components.
     */
    fun getUiManager(): UIManager {
        return uiManager
    }

    fun getTextRenderer(): TextRenderer {
        if (!::textRenderer.isInitialized) {
            textRenderer = TextRenderer(fontManager)
        }
        return textRenderer
    }

    /**
     * Initializes the NanoVG rendering context.
     *
     * This method checks if a previous NanoVG rendering context exists and disposes of
     * it if necessary to prevent resource leaks. After disposing of any existing context,
     * it creates a new NanoVG context with antialiasing and stencil strokes enabled.
     * If the initialization fails, a RuntimeException is thrown.
     *
     * This method is typically called at the beginning of a screen lifecycle
     * or when rebuilding UI components that depend on NanoVG.
     *
     * @throws RuntimeException if the NanoVG context cannot be initialized.
     */
    fun init() {
        dispose()

        vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS or NanoVGGL3.NVG_STENCIL_STROKES)
        if (vg == 0L) {
            throw RuntimeException("Could not initialize NanoVG")
        }

        fontManager.loadFonts()
        if (!::textRenderer.isInitialized) textRenderer = TextRenderer(fontManager)
    }

    /**
     * Renders the user interface using NanoVG within the specified drawing context and dimensions.
     *
     * This method configures the OpenGL state, begins a NanoVG rendering frame,
     * and delegates UI drawing to the `uiManager` before restoring the OpenGL state.
     *
     * @param drawContext the current drawing context used for rendering
     * @param width the width of the rendering area
     * @param height the height of the rendering area
     */
    fun render(drawContext: DrawContext, width: Int, height: Int) {
        if (vg == 0L) return

        val originalBlendEnabled = GL11.glGetBoolean(GL11.GL_BLEND)
        val originalCullFaceEnabled = GL11.glGetBoolean(GL11.GL_CULL_FACE)

        val scale = NVGUtils.getPixelRatio()

        try {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_CULL_FACE)

            NanoVG.nvgBeginFrame(vg, width.toFloat(), height.toFloat(), scale)

            uiManager.draw(vg)

            NanoVG.nvgEndFrame(vg)
        } finally {
            if (!originalBlendEnabled) GL11.glDisable(GL11.GL_BLEND)
            if (originalCullFaceEnabled) GL11.glEnable(GL11.GL_CULL_FACE)

            RenderSystem.enableCull()
            RenderSystem.disableBlend()
        }
    }


    /**
     * Handles a mouse click event by delegating the coordinates to the UIManager.
     *
     * @param mouseX The x-coordinate of the mouse click in pixels.
     * @param mouseY The y-coordinate of the mouse click in pixels.
     */
    fun handleMouseClick(mouseX: Float, mouseY: Float) {
        uiManager.handleMouseClick(mouseX, mouseY)
    }

    /**
     * Handles a key press event and dispatches it to all registered UI components.
     *
     * @param keyCode The integer code of the key that was pressed.
     */
    fun handleKeyPress(keyCode: Int) {
        uiManager.handleKeyPress(keyCode)
    }

    /**
     * Handles mouse movement events by delegating the call to the UI manager.
     *
     * @param mouseX The x-coordinate of the mouse pointer.
     * @param mouseY The y-coordinate of the mouse pointer.
     */
    fun handleMouseMove(mouseX: Float, mouseY: Float) {
        uiManager.handleMouseMove(mouseX, mouseY)
    }

    /**
     * Handles the release of a mouse button at the specified coordinates.
     * Delegates the event to the UIManager to notify all registered components.
     *
     * @param mouseX The x-coordinate of the mouse pointer when the release event occurred.
     * @param mouseY The y-coordinate of the mouse pointer when the release event occurred.
     */
    fun handleMouseRelease(mouseX: Float, mouseY: Float) {
        uiManager.handleMouseRelease(mouseX, mouseY)
    }

    /**
     * Releases the NanoVG rendering context and cleans up resources.
     *
     * This method deletes the NanoVG context if it has been initialized, ensuring the
     * associated resources are properly freed. Once called, the context becomes invalid
     * and cannot be used further. Subsequent calls to this method have no effect.
     */
    fun dispose() {
        // Unload fonts first
        if (vg != 0L) {
            fontManager.unloadFonts()
        }

        // Dispose of the text renderer if it exists
        if (::textRenderer.isInitialized) {
            textRenderer.dispose()
        }

        // Free the NanoVG context
        if (vg != 0L) {
            NanoVGGL3.nvgDelete(vg)
            vg = 0L
        }
    }

}