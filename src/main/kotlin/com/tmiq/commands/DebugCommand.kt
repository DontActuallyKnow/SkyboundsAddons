package com.tmiq.commands

import com.mojang.brigadier.context.CommandContext
import com.tmiq.annotations.Command
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

@Command(
    name = "debug",
    description = "Debug command",
    usage = "/debug"
)
object DebugCommand {

    fun execute(context: CommandContext<FabricClientCommandSource>): Boolean {

    }

}