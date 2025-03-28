package com.tmiq.ui

import com.mojang.blaze3d.systems.RenderSystem
import com.tmiq.utils.NVGUtils
import net.minecraft.client.gui.DrawContext
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL11

object NanoVGRenderer {

    private var vg: Long = 0
    private val uiManager = UIManager()

    fun getVg(): Long {
        return vg;
    }

    fun getUiManager(): UIManager {
        return uiManager;
    }

    fun init () {
        vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS or NanoVGGL3.NVG_STENCIL_STROKES)
        if (vg == 0L) throw RuntimeException("Failed to create NanoVG instance")
    }

    fun render(drawContext: DrawContext, width: Int, height: Int) {
        if (vg == 0L) return

        // Save Minecraft's GL state
        RenderSystem.enableBlend()
        RenderSystem.disableCull()
        val prevBlendSrcFactor = GL11.glGetInteger(GL11.GL_BLEND_SRC)
        val prevBlendDstFactor = GL11.glGetInteger(GL11.GL_BLEND_DST)

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val pixelRatio = NVGUtils.getPixelRatio()

        NanoVG.nvgBeginFrame(vg, width.toFloat(), height.toFloat(), pixelRatio)

        uiManager.draw(vg)

        // End NanoVG frame
        NanoVG.nvgEndFrame(vg)

        // Restore Minecraft's GL state
        RenderSystem.blendFunc(prevBlendSrcFactor, prevBlendDstFactor)
        RenderSystem.enableCull()
    }

    fun handleMouseClick(mouseX: Float, mouseY: Float) {
        uiManager.handleMouseClick(mouseX, mouseY)
    }

    fun handleKeyPress(keyCode: Int) {
        uiManager.handleKeyPress(keyCode)
    }

    fun dispose() {
        if (vg != 0L) {
            NanoVGGL3.nvgDelete(vg)
            vg = 0L
        }
    }

}