package com.tmiq.utils

import com.tmiq.mixin.accessors.BeaconBlockEntityRendererInvoker
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.util.math.BlockPos

object RenderUtils {

    private const val MAX_OVERWORLD_BUILD_HEIGHT = 319
    private val beaconList = mutableMapOf<BlockPos, FloatArray>()

    fun init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(WorldRenderEvents.DebugRender { context ->
            beaconList.forEach { (pos, colorComponents) ->
                renderBeaconBeam(context, pos, colorComponents)
            }
        })

        /*ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            beaconList.clear()
        })*/

    }

    fun addBeaconToRender(pos: BlockPos, colorComponents: FloatArray) {
        if (!beaconList.containsKey(pos)) beaconList[pos] = colorComponents
    }

    private fun renderBeaconBeam(context: WorldRenderContext, pos: BlockPos, colorComponents: FloatArray) {
        //if (FrustumUtils.isVisible(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pos.x.toDouble() + 1.0, MAX_OVERWORLD_BUILD_HEIGHT.toDouble(), pos.z.toDouble() + 1.0)) {
        val matrices = context.matrixStack()
        val camera = context.camera().pos

        matrices.push()
        matrices.translate(pos.x.toDouble() - camera.x, pos.y.toDouble() - camera.y, pos.z.toDouble() - camera.z)

        BeaconBlockEntityRendererInvoker.renderBeam(matrices, context.consumers(), context.tickDelta(), context.world().time, 0, MAX_OVERWORLD_BUILD_HEIGHT, colorComponents)

        matrices.pop()
        //}
    }

}