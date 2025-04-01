package com.tmiq.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.Team
import net.minecraft.text.Text

object ScoreboardUtils {
    fun getScoreboardLines(): List<Text> {
        val scoreboard = MinecraftClient.getInstance().player?.scoreboard ?: return listOf()
        val activeObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return listOf()

        return scoreboard.getScoreboardEntries(activeObjective)
            .filter { !it.hidden() }
            .sortedByDescending { it.value }
            .take(15).map {
                val team = scoreboard.getScoreHolderTeam(it.owner)
                val text = it.name()
                Team.decorateName(team, text)
            }
    }

    fun getScoreboardLine(index: Int): Text? {
        val lines = getScoreboardLines()
        return if (index in lines.indices) lines[index] else null
    }

    fun getScoreboardLineCount(): Int = getScoreboardLines().size

    fun getLastScoreboardLine(): Text? = getScoreboardLine(getScoreboardLineCount() - 1)

    fun getFirstScoreboardLine(): Text? = getScoreboardLine(0)
}