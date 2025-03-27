package com.tmiq

import com.tmiq.annotations.CommandRegistry
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SkyboundsAddons : ModInitializer {

	private val LOGGER: Logger = LoggerFactory.getLogger("skyboundsaddons")
		get() = field

	override fun onInitialize() {
		LOGGER.info("SkyboundsAddons Initializing!")

		CommandRegistry.initialize()

		LOGGER.info("SkyboundsAddons Initialized!")

	}
}