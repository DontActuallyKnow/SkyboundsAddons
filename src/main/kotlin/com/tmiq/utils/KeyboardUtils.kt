package com.tmiq.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil




object KeyboardUtils {

    fun Int.isKeyClicked(): Boolean {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, this)
    }
}