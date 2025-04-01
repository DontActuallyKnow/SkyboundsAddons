package com.tmiq.utils

import com.tmiq.SkyboundsAddons
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient

object LocationUtils {

    var onSkybounds: Boolean = false
        set(value) {
            if (field != value) {
                field = value
            }
        }

    fun initConnectionEvents() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            MinecraftClient.getInstance().execute {
                checkSkyboundsServer()
            }
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            onSkybounds = false
        }
    }

    private fun checkSkyboundsServer() {
        val scoreboardLines = getScoreboardLines()

        onSkybounds = scoreboardLines.any { line ->
            line.string.lowercase().contains("skybounds")
        }

        if (onSkybounds) {
            SkyboundsAddons.LOGGER.info("Detected Skybounds server")
        }

    }

}