package com.tmiq.ui.text

import com.tmiq.ui.NanoVGRenderer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap

class FontManager {

    private val fontHandles = ConcurrentHashMap<String, Int>()
    private val fontBuffers = ConcurrentHashMap<String, ByteBuffer>()
    private val fontPaths = mapOf(
        "regular" to "assets/skyboundsaddons/fonts/roboto-regular.ttf",
        "bold" to "assets/skyboundsaddons/fonts/roboto-bold.ttf"
    )

    init {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
            .registerReloadListener(object: SimpleSynchronousResourceReloadListener {
                override fun getFabricId(): Identifier = Identifier.of("skyboundsaddons", "fonts")

                override fun reload(manager: ResourceManager?) {
                    unloadFonts()
                    loadFonts()
                }
            })
    }

    /**
     * Loads font resources into the NanoVG rendering context and registers them for use.
     *
     * This method iterates through the list of font paths and attempts to load each font into the
     * NanoVG context. If the font is successfully loaded, its corresponding identifier is stored
     * in `fontHandles`. Fonts that fail to load are skipped without throwing exceptions.
     *
     * It relies on `getVg` to obtain the NanoVG context and uses `loadFont` to load individual fonts.
     * If the NanoVG context is not available (indicated by a value of 0L), the method returns immediately.
     *
     * This method is typically called to prepare fonts for usage in rendering operations.
     */
    fun loadFonts() {
        val vg = NanoVGRenderer.getVg()
        if (vg == 0L) return

        fontPaths.forEach{ (name, path) ->
            val fontId = loadFont(vg, name, path)
            if (fontId != -1) {
                fontHandles[name] = fontId
            }
        }
    }

    /**
     * Unloads all currently loaded fonts by clearing the font handles collection.
     * This method is typically used to free up resources by removing any references
     * to the loaded font data. Effective use of this function can help in managing
     * memory and preventing resource leaks when fonts are no longer needed.
     */
    fun unloadFonts() {
        fontHandles.clear()
    }

    /**
     * Loads a font into the NanoVG context and makes it available for rendering.
     *
     * This method reads the font file from the resource path, allocates memory for the font data,
     * and registers it in the NanoVG context under the specified name. If the font fails to load,
     * a value of -1 is returned.
     *
     * @param vg The handle to the NanoVG context where the font will be loaded.
     * @param name The name to assign to the font in the NanoVG context.
     * @param fontPath The path to the font file in the resource directory.
     * @return The font ID assigned by NanoVG if successful, or -1 if the font fails to load.
     */
    private fun loadFont(vg: Long, name: String, fontPath: String): Int {
        try {
            val resourcePath = Identifier.of("skyboundsaddons", fontPath.removePrefix("assets/skyboundsaddons/"))
            val resourceManager = MinecraftClient.getInstance().resourceManager
            val resource = resourceManager.getResource(resourcePath).orElse(null) ?: return -1

            resource.inputStream.use { inputStream ->
                val fontBytes = inputStream.readAllBytes()

                val fontBuffer = MemoryUtil.memAlloc(fontBytes.size)
                fontBuffer.put(fontBytes)
                fontBuffer.flip()

                fontBuffers[name] = fontBuffer

                return NanoVG.nvgCreateFontMem(vg, name, fontBuffer, false)
            }
        } catch (e: Exception) {
            println("Error loading font: ${e.message}")
            e.printStackTrace()
            return -1
        }
    }

    /**
     * Retrieves the font ID associated with the given font name.
     *
     * @param name The name of the font to retrieve.
     * @return The ID of the font as an Int, or null if the font is not found.
     */
    fun getFont(name: String): Int? {
        return fontHandles[name]
    }

    /**
     * Releases the resources and clears the data associated with fonts managed by this object.
     *
     * This method is responsible for ensuring that all font-related resources are properly
     * unloaded and cleaned up. It calls `unloadFonts()` internally to clear font handles from memory,
     * preventing resource leaks. Once called, the `FontManager` instance will no longer manage
     * any fonts until `loadFonts()` is invoked again.
     *
     * Use this method when the `FontManager` is no longer needed or is about to be disposed of.
     */
    fun dispose() {
        unloadFonts()
    }

}