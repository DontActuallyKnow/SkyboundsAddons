package com.tmiq.utils.mc

import com.tmiq.SkyboundsAddons
import com.tmiq.utils.ScoreboardUtils
import com.tmiq.utils.formattedString
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import java.time.Duration

object LocationUtils {

    var onSkybounds: Boolean = false
        set(value) {
            if (field != value) {
                field = value
            }
        }

    fun initConnectionEvents() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            checkSkyboundsServer()
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            onSkybounds = false
        }
    }

    private fun checkSkyboundsServer() {
        var attempts = 0
        val maxAttempts = 5
        val initialDelayMs = 1000

        Thread {
            var delayMs = initialDelayMs

            while (attempts < maxAttempts && !onSkybounds) {
                attempts++
                try {
                    Thread.sleep(Duration.ofMillis(delayMs.toLong()))
                    delayMs *= 2

                    MinecraftClient.getInstance().execute {
                        val scoreboardLine = ScoreboardUtils.getLastScoreboardLine()?.formattedString()

                        if (scoreboardLine != null) {
                            val isOnSkybounds = scoreboardLine.contains("skybounds.com")
                            onSkybounds = isOnSkybounds

                            if (onSkybounds) {
                                SkyboundsAddons.LOGGER.info("Detected Skybounds server")
                            }
                        }
                    }

                    if (onSkybounds) {
                        break
                    }

                } catch (e: InterruptedException) {
                    SkyboundsAddons.LOGGER.error("Scoreboard check interrupted", e)
                    break
                }
            }
        }.start()
    }

}