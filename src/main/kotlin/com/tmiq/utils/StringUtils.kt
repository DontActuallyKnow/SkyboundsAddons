package com.tmiq.utils

object StringUtils {
    private val whiteSpaceResetPattern = "^(?:\\s|§r)*|(?:\\s|§r)*$".toPattern()
    private val whiteSpacePattern = "^\\s*|\\s*$".toPattern()
    private val resetPattern = "(?i)§R".toPattern()
    private val formattingPattern = "(?i)§S".toPattern()
    private val stringColourPattern = "§[0123456789abcdef].*".toPattern()
    private val asciiPattern = "[^\\x00-\\x7F]".toPattern()

    fun String.removeWhitespaceAndResets(): String = whiteSpaceResetPattern.matcher(this).replaceAll("")
    fun String.removeWhitespace(): String = whiteSpacePattern.matcher(this).replaceAll("")
    fun String.removeResets(): String = resetPattern.matcher(this).replaceAll("")
    fun String.removeFormatting(): String = formattingPattern.matcher(this).replaceAll("")
    fun String.removeStringColour(): String = stringColourPattern.matcher(this).replaceAll("")




}