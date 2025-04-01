package com.tmiq.utils.mc

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

object SoundUtils {

    fun playSound(id: Identifier) {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvent.of(id), 1.0f))
    }

    fun playFailure() {
        playSound(Identifier.of("minecraft", "block.anvil.place"))
    }

    fun playSuccess() {
        playDing()
    }

    private fun playDing() {
        playSound(Identifier.of("minecraft", "entity.arrow.hit_player"))
    }
}