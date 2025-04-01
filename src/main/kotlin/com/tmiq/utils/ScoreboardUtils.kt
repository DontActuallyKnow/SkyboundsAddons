package com.tmiq.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.scoreboard.Team
import net.minecraft.text.Text

fun getScoreboardLines(): List<Text> {
    val scoreboard = MinecraftClient.getInstance().player?.scoreboard ?: return listOf()
    val activeObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR) ?: return listOf()

    return scoreboard.getScoreboardEntries(activeObjective)
        .filter { !it.hidden() }
        .take(15).map {
            val team = scoreboard.getScoreHolderTeam(it.owner)
            val text = it.name()
            Team.decorateName(team, text)
        }
}

