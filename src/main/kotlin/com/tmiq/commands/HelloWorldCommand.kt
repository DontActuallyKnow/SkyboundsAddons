package com.tmiq.commands

import com.mojang.brigadier.context.CommandContext
import com.tmiq.annotations.Command
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text

@Command(
    name = "hi",
    aliases = ["hello", "hey", "greetings", "salutation", "greet"],
    description = "Says hello to the player",
    usage = "/hi"
)
object HelloWorldCommand {

    fun execute(context: CommandContext<FabricClientCommandSource>): Boolean {
        context.source.sendFeedback(Text.of("Hello World!"))
        return true
    }
}