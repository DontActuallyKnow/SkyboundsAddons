package com.tmiq

import com.tmiq.annotations.CommandRegistry
import com.tmiq.config.Config
import com.tmiq.utils.LocationUtils
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SkyboundsAddons : ModInitializer {

    companion object {
        const val MOD_ID = "skyboundsaddons"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    }

    override fun onInitialize() {
        LOGGER.info("SkyboundsAddons Initializing!")

        CommandRegistry.initialize()

        // TODO Create annotation for events
        LocationUtils.initConnectionEvents()

        Config.GSON.load()

        LOGGER.info("SkyboundsAddons Initialized!")
    }
}