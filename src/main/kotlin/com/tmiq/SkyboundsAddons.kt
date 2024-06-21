package com.tmiq

import com.tmiq.commands.SkyboundsaddonsCommand
import com.tmiq.dev.BlockBrokenFeature
import com.tmiq.utils.render.RenderUtils
import net.fabricmc.api.ModInitializer

class SkyboundsAddons : ModInitializer {

	override fun onInitialize() {

		println("SkyboundsAddons initialized!")

		SkyboundsaddonsCommand().init()
		RenderUtils().init()
		BlockBrokenFeature().init()
	}

}