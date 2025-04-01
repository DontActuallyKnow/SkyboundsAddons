package com.tmiq.utils

import com.tmiq.SkyboundsAddons
import net.minecraft.client.MinecraftClient

object ClipboardUtils {
    fun setTextContent(string: String) {
        try {
            MinecraftClient.getInstance().keyboard.clipboard = string.ifEmpty { " " }
        } catch (e: Exception) {
            SkyboundsAddons.LOGGER.error("Could not write to clipboard")
        }
    }

    fun getTextContents(): String {
        try {
            return MinecraftClient.getInstance().keyboard.clipboard ?: ""
        } catch (e: Exception) {
            SkyboundsAddons.LOGGER.error("Could not read clipboard")
            return ""
        }
    }
}