package com.tmiq.utils

import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class Utils {

    /**
     * Returns the color code character
     *
     * @return Minecaft color code char
     */
    fun getColorCodeChar(): Char {
        return '§'
    }

    /**
     * Convert BlockPos into Vec3d
     *
     * @param pos BlockPos input
     * @return Vec3d output
     */
    fun blockPosToVec(pos: BlockPos): Vec3d {
        val x = pos.x
        val y = pos.y
        val z = pos.z

        val vector = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
        return vector
    }

    /**
     * Convert Vec3d into BlockPos
     *
     * @param vec Vec3d input
     * @return BlockPos output
     */
    fun vecToBlockPos(vec: Vec3d): BlockPos {
        val x = vec.x
        val y = vec.y
        val z = vec.z

        val pos = BlockPos(x.toInt(), y.toInt(), z.toInt())
        return pos
    }

    /**
     * Send chat message to player. Converts & for colour codes
     *
     * @param str Message to send
     */
    fun sendChatMessage(str: String) {
        val client = MinecraftClient.getInstance()

        val message = c(str, '&')
        client.inGameHud.chatHud.addMessage(Text.literal(message))
    }

    /**
     * Translate specified symbol for color codes
     *
     * @param str Message to translate
     * @param char Custom color code
     * @return Message with translated color codes
     */
    fun c(str: String, char: Char): String {
        return str.replace(char, getColorCodeChar())
    }

}