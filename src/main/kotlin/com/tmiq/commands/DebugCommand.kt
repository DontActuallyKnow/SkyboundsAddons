package com.tmiq.commands

import com.mojang.brigadier.context.CommandContext
import com.tmiq.annotations.Command
import com.tmiq.utils.ClipboardUtils
import com.tmiq.utils.ScoreboardUtils
import com.tmiq.utils.Utils
import com.tmiq.utils.formattedString
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

@Command(
    name = "debug",
    description = "Debug command",
    usage = "/debug <scoreboard|...>"
)
object DebugCommand {

    fun execute(context: CommandContext<FabricClientCommandSource>): Boolean {
        context.source.sendFeedback(Utils.translateChat("&6Copying scoreboard lines to clipboard"))

        val scoreboardLines = ScoreboardUtils.getScoreboardLines()
        val formattedScoreboardLines = scoreboardLines.map { it.formattedString() }
        ClipboardUtils.setTextContent(formattedScoreboardLines.joinToString("\n"))

        val lastScoreboardLine = ScoreboardUtils.getLastScoreboardLine()
        context.source.sendFeedback(Utils.translateChat("&6Last scoreboard line: &r${lastScoreboardLine?.formattedString()}"))

        return true
    }

}