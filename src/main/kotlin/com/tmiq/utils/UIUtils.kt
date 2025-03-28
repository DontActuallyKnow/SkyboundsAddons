package com.tmiq.utils

import com.tmiq.SkyboundsAddons
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen

object UIUtils {
    private var previousScreen: Screen? = null

    /**
     * Sets the current screen in the Minecraft client while preserving the previous screen
     * in history for potential later use.
     *
     * @param screen The screen to set as the current screen.
     */
    fun setScreenWithHistory(screen: Screen) {
        previousScreen = MinecraftClient.getInstance().currentScreen
        MinecraftClient.getInstance().send {
            MinecraftClient.getInstance().setScreen(screen)
        }
    }

    /**
     * Sets the current screen in the Minecraft client.
     *
     * @param screen The screen to be set as the current screen.
     */
    fun setScreen(screen: Screen) {
        MinecraftClient.getInstance().send {
            MinecraftClient.getInstance().setScreen(screen)
        }
    }

    /**
     * Closes the current screen in the Minecraft client and navigates to the previous screen if available.
     * If no previous screen is set, it will clear the screen by setting it to null.
     */
    fun closeScreen() {
        if (previousScreen != null) {
            MinecraftClient.getInstance().send {
                MinecraftClient.getInstance().setScreen(previousScreen)
            }
            previousScreen = null
        } else {
            MinecraftClient.getInstance().send {
                MinecraftClient.getInstance().setScreen(null)
            }
        }
    }
}