package com.tmiq.utils.render

import com.mojang.blaze3d.systems.RenderSystem
import com.tmiq.mixin.accessors.BeaconBlockEntityRendererInvoker
import com.tmiq.utils.Utils
import com.tmiq.utils.render.layers.ESPLayer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BlockOutline
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import java.awt.Color


class RenderUtils {

    private val MAX_OVERWORLD_BUILD_HEIGHT = 319

    private val beaconList = mutableMapOf<BlockPos, FloatArray>()

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
                BlockPos(0, 100, 0),
                Text.literal(Utils().c("&4T&6e&cs&dt", '&')),
                context.camera(),
                true
            )

            val ONE = Vec3d(1.0, 1.0, 1.0)
            if (MinecraftClient.getInstance().world != null || MinecraftClient.getInstance().player != null) {
                renderFilled(context, BlockPos(0, 99, 0), ONE, Color(100, 0, 100), 0.4f, true, true);
            }
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
        matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?, pos: BlockPos,
        text: Text, camera: Camera, seeThroughBlocks: Boolean
    ) {
        renderTextAtBlockPos(
            matrixStack,
            vertexConsumerProvider,
            Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
            text,
            camera,
            seeThroughBlocks
        )
    }

    fun renderTextAtBlockPos(
        matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?, pos: Vec3d,
        text: Text, camera: Camera, seeThroughBlocks: Boolean
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

        RenderSystem.depthFunc(if (seeThroughBlocks) GL11.GL_ALWAYS else GL11.GL_LEQUAL)

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

        RenderSystem.depthFunc(GL11.GL_LEQUAL)

        matrixStack.pop()
    }

    private fun renderFilled(
        context: WorldRenderContext,
        blockPos: BlockPos,
        dimensions: Vec3d,
        color: Color,
        alpha: Float,
        outline: Boolean,
        throughWalls: Boolean
    ) {
        if(alpha == 0f) return

        val matrices = context.matrixStack()
        val camera = context.camera().pos

        val pos = Vec3d.of(blockPos)

        matrices.push()
        matrices.translate(-camera.x, -camera.y, -camera.z)

        val consumers = context.consumers()
        val renderLayer = RenderLayer.getDebugFilledBox()
        val vertexConsumer = consumers?.getBuffer(renderLayer)

        WorldRenderer.renderFilledBox(
            matrices, vertexConsumer, pos.x, pos.y, pos.z, pos.x + dimensions.x, pos.y + dimensions.y, pos.z + dimensions.z,
            color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), alpha
        )
        matrices.pop()


        if(outline) {
            val updatedAlpha = alpha * 1.5f
            val clampedAlpha = updatedAlpha.coerceIn(0.0f, 1.0f)
            renderBlockOutline(context, blockPos, color, clampedAlpha, throughWalls)
        }

    }

    fun renderBlockOutline(
        context: WorldRenderContext,
        blockPos: BlockPos,
        color: Color,
        alpha: Float,
        throughWalls: Boolean
    ) {
        if(alpha == 0f) return

        // Get the position of the block and the camera
        val camera = context.camera()
        val cameraPos: Vec3d = camera.pos
        val offsetX = blockPos.x - cameraPos.x
        val offsetY = blockPos.y - cameraPos.y
        val offsetZ = blockPos.z - cameraPos.z

        // Push the matrix stack
        val matrices: MatrixStack = context.matrixStack()
        matrices.push()
        matrices.translate(offsetX, offsetY, offsetZ)

        // Create a vertex consumer
        val vertexConsumerProvider: VertexConsumerProvider? = context.consumers()
        val buffer: VertexConsumer = vertexConsumerProvider!!.getBuffer(RenderLayer.getLines()) // TODO: Add way to make transparent??
        val matrix4f = matrices.peek().positionMatrix
        val matrix3f = matrices.peek().normalMatrix

        // Draw the outline for each edge of the block

        val blockOutlineColor = color
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 0f, 0f, 1f, 0f, 0f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 0f, 0f, 0f, 1f, 0f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 0f, 0f, 0f, 0f, 1f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 1f, 1f, 1f, 0f, 1f, 1f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 1f, 1f, 1f, 1f, 0f, 1f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 1f, 1f, 1f, 1f, 1f, 0f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 1f, 0f, 0f, 1f, 1f, 0f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 1f, 0f, 0f, 1f, 0f, 1f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 1f, 0f, 1f, 1f, 0f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 1f, 0f, 0f, 1f, 1f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 0f, 1f, 1f, 0f, 1f, alpha)
        drawLine(buffer, matrix4f, matrix3f, blockOutlineColor, 0f, 0f, 1f, 0f, 1f, 1f, alpha)

        matrices.pop()
    }

    fun drawLine(
        buffer: VertexConsumer,
        matrix4f: Matrix4f,
        matrix3f: Matrix3f,
        color: Color,
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        alpha: Float
    ) {

        val red = color.red.toFloat()
        val green =color.green.toFloat()
        val blue = color.blue.toFloat()

        buffer.vertex(matrix4f, startX, startY, startZ)
            .color(red, green, blue, alpha)
            .normal(matrix3f, 0.0f, 1.0f, 0.0f)
            .next()
        buffer.vertex(matrix4f, endX, endY, endZ)
            .color(red, green, blue, alpha)
            .normal(matrix3f, 0.0f, 1.0f, 0.0f)
            .next()
    }

    /**
     * Converts a BlockPos to a Box (aabb)
     *
     * @param pos BlockPos
     * @return a Box with min and max values
     */
    fun convertToBox(pos: BlockPos): Box {
        val minX = pos.x - 0.5
        val minY = pos.y - 0.5
        val minZ = pos.z - 0.5
        val maxX = pos.x + 0.5
        val maxY = pos.y + 0.5
        val maxZ = pos.z + 0.5

        return Box(minX, minY, minZ, maxX, maxY, maxZ)
    }

    /**
     * Converts a Box to a BlockPos
     *
     * @param box Box
     * @return a BlockPos with rounded values for x y z
     */
    fun convertToBlockPos(box: Box): BlockPos {
        val centerX = (box.maxX + box.minX) / 2.0
        val centerY = (box.maxY + box.minY) / 2.0
        val centerZ = (box.maxZ + box.minZ) / 2.0

        val x = Math.round(centerX).toInt()
        val y = Math.round(centerY).toInt()
        val z = Math.round(centerZ).toInt()

        return BlockPos(x, y, z)
    }

    /**
     * Convert a BlockPos to Vec3d. Assumes middle of block
     *
     * @param pos
     * @return Vec3d
     */
    fun blockPosToVec(pos: BlockPos): Vec3d {
        val x = pos.x + 0.5
        val y = pos.y + 0.5
        val z = pos.z + 0.5

        return Vec3d(x, y, z)
    }

}