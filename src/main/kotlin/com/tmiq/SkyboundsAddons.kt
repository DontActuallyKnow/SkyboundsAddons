package com.tmiq

import com.tmiq.commands.SkyboundsaddonsCommand
import com.tmiq.dev.BlockBrokenFeature
import com.tmiq.utils.hud.TestElement
import com.tmiq.utils.render.RenderUtils
import com.tmiq.utils.render.WaypointHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback


class SkyboundsAddons : ModInitializer {

	override fun onInitialize() {

		println("SkyboundsAddons initialized!")

		SkyboundsaddonsCommand().init()

		WaypointHandler.init()
		RenderUtils.init()
		BlockBrokenFeature().init()
		HudRenderCallback.EVENT.register(TestElement())
	}

}