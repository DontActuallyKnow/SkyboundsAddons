package com.tmiq.utils

import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*
import java.util.regex.Pattern

object StringUtils {

    private val UUID_PATTERN =
        Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", Pattern.CASE_INSENSITIVE)

    fun isValidUUID(input: String): Boolean {
        return UUID_PATTERN.matcher(input).matches()
    }

    fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")
            append("${seconds}s")
        }.trim()
    }

    fun capitalizeWords(input: String): String {
        return input.split(" ")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.substring(0, 1).uppercase() + word.substring(1).lowercase()
            }
    }
}

fun Text.formattedString(): String {
    val sb = StringBuilder()
    visit(StringVisitable.StyledVisitor<Unit> { style, string ->
        val c = Formatting.byName(style.color?.name)
        if (c != null) {
            sb.append("§${c.code}")
        }
        if (style.isUnderlined) {
            sb.append("§n")
        }
        if (style.isBold) {
            sb.append("§l")
        }
        sb.append(string)
        Optional.empty()
    }, Style.EMPTY)
    return sb.toString().replace("§[^a-f0-9]".toRegex(), "")
}