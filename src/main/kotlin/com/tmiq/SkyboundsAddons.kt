package com.tmiq

import com.tmiq.commands.SkyboundsaddonsCommand
import net.fabricmc.api.ModInitializer

object SkyboundsAddons : ModInitializer {

	override fun onInitialize() {

		println("SkyboundsAddons initialized!")

		SkyboundsaddonsCommand.init()
	}

}