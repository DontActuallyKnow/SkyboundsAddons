package com.tmiq.ui

import com.tmiq.config.Config
import com.tmiq.utils.LocationUtils
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
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
                        .option(
                            Option.createBuilder<Boolean>()
                                .name(Text.literal("Mod enabled"))
                                .binding(
                                    defaults.modEnabled,
                                    { config.modEnabled },
                                    { config.modEnabled = it }
                                )
                                .description(OptionDescription.of(Text.literal("Enables the mod")))
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(
                            Option.createBuilder<Boolean>()
                                .name(Text.literal("On Skybounds"))
                                .binding(
                                    false,
                                    { LocationUtils.onSkybounds },
                                    { LocationUtils.onSkybounds = it }
                                )
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build()
                )
                .save {
                    MinecraftClient.getInstance().options.write()
                    Config.GSON.save()
                }
        }.generateScreen(parent)
    }

}