package com.tmiq.commands

import com.tmiq.utils.render.Waypoint
import com.tmiq.utils.render.WaypointHandler
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import java.awt.Color

class SkyboundsaddonsCommand {

    var inside = false

    //TODO: Implement MoulConfig after port
    fun init() {
        //val config = ManagedConfig.create(File("config/sba/config.json"), Config::class.java)

        val aliases = listOf("sba", "skyboundsaddons", "sbaddons", "skyaddons")
        aliases.forEach { alias ->
            ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

                val command = literal(alias).executes {
                    /*MinecraftClient.getInstance().send {
                        config.openConfigGui()
                    }*/

                    val message = Text.literal("§aHello").styled { style ->
                        style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("§cwhatever")))
                    }

                    val myWaypoint = Waypoint(Text.literal("§aHello"), BlockPos(6, 99, 0), Color(158, 77, 179), 0.5f,
                        false, true, '5', 200.0, 1f)
                    if(inside) {
                        WaypointHandler.removeWaypoint(myWaypoint)
                        //WaypointHandler.clearWaypoints()
                        inside = !inside
                    } else {
                        WaypointHandler.addWaypoint(myWaypoint)
                        inside = !inside
                    }

                    MinecraftClient.getInstance().inGameHud.chatHud.addMessage(message)

                    0
                }

                dispatcher.register(command)
            }
        }
    }


}