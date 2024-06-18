package com.tmiq.utils.render

import com.mojang.blaze3d.systems.RenderSystem
import com.tmiq.mixin.accessors.BeaconBlockEntityRendererInvoker
import com.tmiq.utils.Utils
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.Camera
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11

class RenderUtils {

    private val MAX_OVERWORLD_BUILD_HEIGHT = 319

    private val beaconList = mutableMapOf<BlockPos, FloatArray>()
    private val cc = Utils().getColorCodeChar()

    fun init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register { context ->
            beaconList.forEach { (pos, colorComponents) ->
                renderBeaconBeam(context, pos, colorComponents)
            }
        }

        ServerWorldEvents.UNLOAD.register { _, _ ->
            beaconList.clear()
        }

        WorldRenderEvents.AFTER_TRANSLUCENT.register { context ->
            val matrixStack = context.matrixStack()
            renderTextAtBlockPos(
                matrixStack,
                context.consumers(),
                Vec3d(0.5, 100.5, 0.5),
                Text.literal(Utils().c("&4T&6e&cs&dt", '&')),
                context.camera(),
                true
            )
        }

    }

    fun addBeaconToRender(pos: BlockPos, colorComponents: FloatArray) {
        if (!beaconList.containsKey(pos)) beaconList[pos] = colorComponents
    }

    private fun renderBeaconBeam(context: WorldRenderContext, pos: BlockPos, colorComponents: FloatArray) {
        val matrices = context.matrixStack()
        val camera = context.camera().pos

        matrices.push()
        matrices.translate(pos.x.toDouble() - camera.x, pos.y.toDouble() - camera.y, pos.z.toDouble() - camera.z)

        BeaconBlockEntityRendererInvoker.renderBeam(
            matrices,
            context.consumers(),
            context.tickDelta(),
            context.world().time,
            0,
            MAX_OVERWORLD_BUILD_HEIGHT,
            colorComponents
        )

        matrices.pop()
        //}
    }

    fun renderTextAtBlockPos(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider?,
        pos: BlockPos,
        text: Text,
        camera: Camera,
        seeThroughBlocks: Boolean
    ) {
        val client = MinecraftClient.getInstance()
        val cameraPos = camera.pos

        val x = (pos.x - cameraPos.x) + 0.5
        val y = (pos.y - cameraPos.y) + 0.5
        val z = (pos.z - cameraPos.z) + 0.5

        val rotation = camera.rotation

        matrixStack.push()

        matrixStack.translate(x, y, z)

        matrixStack.multiply(rotation)

        matrixStack.scale(-0.025f, -0.025f, 0.025f)

        val matrix4f: Matrix4f = matrixStack.peek().positionMatrix

        val textRenderer: TextRenderer = client.textRenderer
        val offset = ((-textRenderer.getWidth(text) / 2).toFloat())

        if (seeThroughBlocks) {
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.depthMask(false)
        }

        textRenderer.draw(
            text.literalString,
            offset,
            0f,
            0,
            false,
            matrix4f,
            vertexConsumerProvider,
            TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

        matrixStack.pop()
    }

    fun renderTextAtBlockPos(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider?,
        pos: Vec3d,
        text: Text,
        camera: Camera,
        seeThroughBlocks: Boolean
    ) {
        val client = MinecraftClient.getInstance()
        val cameraPos = camera.pos

        val x = (pos.x - cameraPos.x)
        val y = (pos.y - cameraPos.y)
        val z = (pos.z - cameraPos.z)

        val rotation = camera.rotation

        matrixStack.push()

        matrixStack.translate(x, y, z)

        matrixStack.multiply(rotation)

        matrixStack.scale(-0.025f, -0.025f, 0.025f)

        val matrix4f: Matrix4f = matrixStack.peek().positionMatrix

        val textRenderer: TextRenderer = client.textRenderer
        val offset = ((-textRenderer.getWidth(text) / 2).toFloat())

        if (seeThroughBlocks) {
            RenderSystem.disableDepthTest()  // Disable depth testing
            RenderSystem.enableBlend()  // Enable blending
            RenderSystem.defaultBlendFunc()  // Use the default blend function
            RenderSystem.depthMask(false)  // Disable depth mask to avoid writing to the depth buffer
        }

        textRenderer.draw(
            text.literalString,
            offset,
            0f,
            0,
            false,
            matrix4f,
            vertexConsumerProvider,
            TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

        if (seeThroughBlocks) {
            RenderSystem.depthMask(true)  // Re-enable depth mask
            RenderSystem.enableDepthTest()  // Re-enable depth testing
            RenderSystem.disableBlend()  // Disable blending
        }

        matrixStack.pop()
    }

}