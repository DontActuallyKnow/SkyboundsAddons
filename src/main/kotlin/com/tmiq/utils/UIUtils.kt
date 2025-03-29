package com.tmiq.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen

object UIUtils {
    private var previousScreen: Screen? = null

    /**
     * Sets the given screen as the current screen while storing a reference
     * to the previous screen for potential future use.
     *
     * @param screen The new screen to be displayed.
     */
    fun setScreenWithHistory(screen: Screen) {
        previousScreen = MinecraftClient.getInstance().currentScreen
        MinecraftClient.getInstance().send {
            MinecraftClient.getInstance().setScreen(screen)
        }
    }

    /**
     * Sets the current screen of the Minecraft client to the specified screen.
     *
     * @param screen The screen to set as the current screen.
     */
    fun setScreen(screen: Screen) {
        MinecraftClient.getInstance().send {
            MinecraftClient.getInstance().setScreen(screen)
        }
    }

    /**
     * Closes the current screen in the Minecraft client and navigates back to the previous screen if it exists,
     * or sets the screen to null if there is no previous screen.
     *
     * If a previous screen is present, the method sets the Minecraft client's screen to the previous screen
     * and then clears the previous screen reference.
     * If no previous screen is available, the method sets the client's screen to null.
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