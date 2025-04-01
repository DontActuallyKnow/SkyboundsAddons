package com.tmiq.commands

import com.mojang.brigadier.context.CommandContext
import com.tmiq.annotations.Command
import com.tmiq.ui.ConfigGui
import com.tmiq.utils.UIUtils
import com.tmiq.utils.Utils
import com.tmiq.utils.mc.SoundUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient

@Command(
    name = "hi",
    aliases = ["hello", "hey", "greetings", "salutation", "greet"],
    description = "Says hello to the player",
    usage = "/hi"
)
object HelloWorldCommand {

    fun execute(context: CommandContext<FabricClientCommandSource>): Boolean {
        context.source.sendFeedback(Utils.translateChat("&6Hello gang"))

        val parent = MinecraftClient.getInstance().currentScreen
        parent?.let { ConfigGui.openConfigGui(it) }?.let { UIUtils.setScreen(it) }

        SoundUtils.playSuccess()

        return true
    }
}