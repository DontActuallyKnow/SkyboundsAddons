@file:Suppress("unused")

package com.tmiq.commands

import com.mojang.brigadier.context.CommandContext
import com.tmiq.annotations.Argument
import com.tmiq.annotations.Command
import com.tmiq.annotations.Subcommand
import com.tmiq.utils.ClipboardUtils
import com.tmiq.utils.ScoreboardUtils
import com.tmiq.utils.Utils
import com.tmiq.utils.formattedString
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack

@Command(
    name = "debug",
    description = "Debug command",
    usage = "/debug <scoreboard|hand|slot|...>",
    hasSubcommands = true
)
object DebugCommand {

    fun execute(context: CommandContext<FabricClientCommandSource>): Int {
        context.source.sendFeedback(Utils.translateChat("&6Available debug commands:"))
        context.source.sendFeedback(Utils.translateChat("&7- /debug scoreboard: Copy scoreboard lines"))
        context.source.sendFeedback(Utils.translateChat("&7- /debug hand: Show details about item in hand"))
        context.source.sendFeedback(Utils.translateChat("&7- /debug slot <number>: Show details about item in specific slot"))

        return 1
    }

    @Subcommand(
        name = "scoreboard",
        description = "Copy scoreboard lines to clipboard"
    )
    fun scoreboard(context: CommandContext<FabricClientCommandSource>): Int {
        context.source.sendFeedback(Utils.translateChat("&6Copying scoreboard lines to clipboard"))

        val scoreboardLines = ScoreboardUtils.getScoreboardLines()
        val formattedScoreboardLines = scoreboardLines.map { it.formattedString() }
        ClipboardUtils.setTextContent(formattedScoreboardLines.joinToString("\n"))

        val lastScoreboardLine = ScoreboardUtils.getLastScoreboardLine()
        context.source.sendFeedback(Utils.translateChat("&6Last scoreboard line: &r${lastScoreboardLine?.formattedString()}"))

        return 1
    }

    @Subcommand(
        name = "hand",
        description = "Shows information about the item in your hand"
    )
    fun hand(context: CommandContext<FabricClientCommandSource>): Int {
        context.source.sendFeedback(Utils.translateChat("&6Showing info about item in hand"))

        val player = context.source.player
        val handItem = player.mainHandStack

        if (handItem == ItemStack.EMPTY) {
            context.source.sendFeedback(Utils.translateChat("&cNo item in main hand"))
            return 0
        }

        displayItemInfo(context, handItem)
        return 1
    }

    @Subcommand(
        name = "slot",
        description = "Shows information about an item in a specific slot"
    )
    fun slot(
        context: CommandContext<FabricClientCommandSource>,
        @Argument(name = "slotNumber", description = "The slot number to check (0-8 for hotbar, 9-35 for inventory)")
        slotNumber: Int
    ): Int {
        if (slotNumber < 0 || slotNumber > 35) {
            context.source.sendFeedback(Utils.translateChat("&cInvalid slot number. Must be between 0-35"))
            return 0
        }

        context.source.sendFeedback(Utils.translateChat("&6Showing info about item in slot $slotNumber"))

        val player = context.source.player
        val inventory = player.inventory
        val itemInSlot = inventory.getStack(slotNumber)

        if (itemInSlot == ItemStack.EMPTY) {
            context.source.sendFeedback(Utils.translateChat("&cNo item in slot $slotNumber"))
            return 0
        }

        displayItemInfo(context, itemInSlot)
        return 1
    }

    private fun displayItemInfo(context: CommandContext<FabricClientCommandSource>, item: ItemStack) {
        context.source.sendFeedback(Utils.translateChat("&6Item: &r${item.name.string}"))
        context.source.sendFeedback(Utils.translateChat("&6ID: &r${item.item.translationKey}"))
        context.source.sendFeedback(Utils.translateChat("&6Count: &r${item.count}"))
        context.source.sendFeedback(Utils.translateChat("&6Lore: &r${item.components.get(DataComponentTypes.LORE)}"))
    }

}