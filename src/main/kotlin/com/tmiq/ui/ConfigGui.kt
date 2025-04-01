package com.tmiq.ui

import com.tmiq.config.Config
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

// TODO Add translations (Server is 50% german / dutch)
class ConfigGui {

    companion object {
        fun openConfigGui(parent: Screen): Screen {
            return ConfigGui().getFullConfig(parent)
        }
    }

    fun getFullConfig(parent: Screen): Screen {
        Config.GSON.load()

        return YetAnotherConfigLib.create(Config.GSON) { defaults, config, builder ->
            builder
                .title(Text.literal("Test GUI"))
                .category(
                    ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .tooltip(Text.literal("Global settings"))

                        .build()
                )
                .save {
                    MinecraftClient.getInstance().options.write()
                    Config.GSON.save()
                }
        }.generateScreen(parent)
    }

}