package com.tmiq

import com.tmiq.commands.SkyboundsaddonsCommand
import com.tmiq.config.Config
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import java.io.File

object SkyboundsAddons : ClientModInitializer {

	fun createConfig(): ManagedConfig<Config> {
		val config = ManagedConfig.create(File("config/sba/config.json"), Config::class.java)
		return config
	}

	override fun onInitializeClient() {
		val config = createConfig()

		val aliases = listOf("sba", "skyboundsaddons", "sbaddons", "skyaddons")
		aliases.forEach { alias ->
			ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->

				val command = literal(alias).executes {
					MinecraftClient.getInstance().send {
						config.openConfigGui()
					}
					0
				}

				dispatcher.register(command)
			}
		}
		println("SkyboundsAddons initialized!")

		//SkyboundsaddonsCommand.init()

	}

}