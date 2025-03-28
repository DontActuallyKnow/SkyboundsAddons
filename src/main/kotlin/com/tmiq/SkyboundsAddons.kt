package com.tmiq

import com.tmiq.annotations.CommandRegistry
import com.tmiq.events.ClientHooks
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SkyboundsAddons : ModInitializer {

	private val LOGGER: Logger = LoggerFactory.getLogger("skyboundsaddons")
		get() = field

	override fun onInitialize() {
		LOGGER.info("SkyboundsAddons Initializing!")

		CommandRegistry.initialize()
		ClientHooks.register() // TODO: Implement annotation to register classes annotated with @Event

		LOGGER.info("SkyboundsAddons Initialized!")

	}
}