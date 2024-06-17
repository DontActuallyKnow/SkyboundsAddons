package com.tmiq.utils

import java.util.*

object NumberUtils {

    private fun getDecimalValue(romanChar: Char): Int {
        return when (romanChar) {
            'I' -> 1
            'V' -> 5
            'X' -> 10
            'L' -> 50
            'C' -> 100
            'D' -> 500
            'M' -> 1000
            else -> 0
        }
    }

    /**
     * Checks if a string is a valid roman numeral.
     * It's the caller's responsibility to clean up the string before calling this method (such as trimming it).
     * @param romanNumeral The roman numeral to check.
     * @return True if the string is a valid roman numeral, false otherwise.
     * @implNote This will only check if the string contains valid roman numeral characters. It won't check if the numeral is well-formed.
     */
    fun isValidRomanNumeral(romanNumeral: String?): Boolean {
        if (romanNumeral.isNullOrEmpty()) return false
        for (element in romanNumeral) {
            if (getDecimalValue(element) == 0) return false
        }
        return true
    }

    /**
     * Converts a roman numeral to a decimal number.
     *
     * @param romanNumeral The roman numeral to convert.
     * @return The decimal number, or 0 if the roman numeral string has non-roman characters in it, or if the string is empty or null.
     */
    fun romanToDecimal(romanNumeral: String?): Int {
        if (romanNumeral.isNullOrEmpty()) return 0
        val romanNumeral = romanNumeral.trim { it <= ' ' }.uppercase(Locale.getDefault())
        var decimal = 0
        var lastNumber = 0
        for (i in romanNumeral.length - 1 downTo 0) {
            val ch = romanNumeral[i]
            val number = getDecimalValue(ch)
            if (number == 0) return 0 //Malformed roman numeral
            decimal = if (number >= lastNumber) decimal + number else decimal - number
            lastNumber = number
        }
        return decimal
    }



}