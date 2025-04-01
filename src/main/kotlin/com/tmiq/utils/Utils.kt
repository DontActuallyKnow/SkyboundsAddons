package com.tmiq.utils

import net.minecraft.text.MutableText
import net.minecraft.text.Text

object Utils {

    /**
     * Because im lazy
     *
     * @param text The input string containing chat text with '&' color codes.
     * @return A Text object with the translated chat message using Minecraft's formatting codes.
     */
    fun translateChat(text: String): MutableText {
        return Text.literal(text.replace('&', '§'))
    }

}