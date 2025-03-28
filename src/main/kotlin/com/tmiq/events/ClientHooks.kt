package com.tmiq.events

import com.tmiq.ui.NanoVGRenderer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.Screen

object ClientHooks {
    fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenEvents.remove(screen).register { screen: Screen ->
                if (screen is com.tmiq.Screen) {
                    screen.removed()
                }
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            NanoVGRenderer.dispose()
        }
    }
}
