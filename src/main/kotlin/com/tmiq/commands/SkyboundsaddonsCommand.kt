package com.tmiq.commands

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object SkyboundsaddonsCommand {

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
                    0
                }

                dispatcher.register(command)
            }
        }
    }


}