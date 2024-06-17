package com.tmiq

import com.tmiq.utils.RenderUtils
import net.fabricmc.api.ClientModInitializer

object Skyboundsplus : ClientModInitializer {

	override fun onInitializeClient() {
		println("Hello, Fabric world!")

		RenderUtils.init()

	}

}