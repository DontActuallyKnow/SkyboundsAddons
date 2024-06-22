package com.tmiq.utils.render

import com.mojang.blaze3d.systems.RenderSystem
import com.tmiq.mixin.accessors.BeaconBlockEntityRendererInvoker
import com.tmiq.utils.NumberUtils.format
import com.tmiq.utils.TimeUnit
import com.tmiq.utils.Utils
import com.tmiq.utils.render.FrustumUtils.isVisible
import com.tmiq.utils.render.layers.FilledRenderLayer
import com.tmiq.utils.render.layers.FilledThroughWallsRenderLayer
import com.tmiq.utils.time.TimeMarker
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.awt.Color


object RenderUtils {

    private const val MAX_OVERWORLD_BUILD_HEIGHT = 319
    private val ONE = Vec3d(1.0, 1.0, 1.0)

    fun init() {
        val marker = TimeMarker.now()

        WorldRenderEvents.AFTER_TRANSLUCENT.register { context ->

            val passedSince = marker.passedSince()
            val timeFormat = passedSince.format(TimeUnit.MINUTE)

            renderText(context, Text.literal(Utils.c("&4Time since init: &a${timeFormat}", '&')), BlockPos(0, 100, 0), true)

            renderOutline(context, BlockPos(1, 100, 1), Color(100, 0, 100), 5f, 1f, false)

            renderBox(context, BlockPos(2, 99, 0), Color(100, 0, 100), 0.3f, true, true)

            renderBoxWithBeam(context, BlockPos(4, 99, 0), Color(201, 0, 220), 0.5f, false, true)

        }
    }


    private fun renderBeaconBeam(
        context: WorldRenderContext, pos: BlockPos, color: Color
    ) {
        val red = color.red.toFloat()
        val green = color.green.toFloat()
        val blue =  color.blue.toFloat()

        val colorComponents = floatArrayOf(red, green, blue)

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
    }


    fun renderBoxWithBeam(
        context: WorldRenderContext, pos: BlockPos, color: Color, alpha: Float, throughWalls: Boolean, outline: Boolean
    ) {
        renderBox(context, pos, color, alpha, throughWalls, outline)
        val beaconPos = BlockPos(pos.x, pos.y + 1, pos.z)
        renderBeaconBeam(context, beaconPos, color)
    }


    fun renderText(
        context: WorldRenderContext, text: Text, pos: BlockPos, throughWalls: Boolean
    ) {
        renderText(context, text, pos, 1f, throughWalls)
    }

    fun renderText(
        context: WorldRenderContext, text: Text, pos: BlockPos, scale: Float, throughWalls: Boolean
    ) {
        renderText(context, text, blockPosToCenterVec(pos), scale, 0f, throughWalls)
    }

    fun renderText(
        context: WorldRenderContext, text: Text, pos: Vec3d, scale: Float, yOffset: Float, throughWalls: Boolean
    ) {
        val client = MinecraftClient.getInstance()

        val matrices = context.matrixStack()
        val camera = context.camera().pos
        val textRenderer: TextRenderer = client.textRenderer

        val adjustedScale = scale * 0.025f

        matrices.push()
        matrices.translate(pos.x - camera.x, pos.y - camera.y, pos.z - camera.z)
        matrices.peek().positionMatrix.mul(RenderSystem.getModelViewMatrix())
        matrices.multiply(context.camera().rotation)
        matrices.scale(-adjustedScale, -adjustedScale, adjustedScale)

        val positionMatrix = matrices.peek().positionMatrix
        val xOffset = -textRenderer.getWidth(text) / 2f

        val tessellator = RenderSystem.renderThreadTesselator()
        val buffer = tessellator.buffer
        val consumers = VertexConsumerProvider.immediate(buffer)

        RenderSystem.depthFunc(if (throughWalls) GL11.GL_ALWAYS else GL11.GL_LEQUAL)
        val textLayerType = if (throughWalls) TextLayerType.SEE_THROUGH else TextLayerType.NORMAL

        textRenderer.draw(text, xOffset, yOffset, 0xFFFFFF, false, positionMatrix, consumers,
            textLayerType, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE)
        consumers.draw()

        RenderSystem.depthFunc(GL11.GL_EQUAL)
        matrices.pop()
    }


    fun renderBox(
        context: WorldRenderContext, pos: BlockPos, color: Color, alpha: Float, throughWalls: Boolean, outline: Boolean
    ) {
        renderFilled(context, pos, color, alpha, throughWalls)
        if(outline) {
            val updatedAlpha = alpha * 1.5f
            val clampedAlpha = updatedAlpha.coerceIn(0.0f, 1.0f)
            renderOutline(context, pos, color, 2.5f, clampedAlpha, throughWalls)
        }
    }


    fun renderFilled(
        context: WorldRenderContext, pos: BlockPos, color: Color, alpha: Float, throughWalls: Boolean
    ) {
        renderFilled(context, blockPosToVec(pos), ONE, color, alpha, throughWalls)
    }

    private fun renderFilled(
        context: WorldRenderContext, pos: Vec3d, dimensions: Vec3d, color: Color, alpha: Float, throughWalls: Boolean
    ) {

        if(!throughWalls && !isVisible(Box.from(pos))) return
        val matrices = context.matrixStack()
        val camera = context.camera().pos

        matrices.push()
        matrices.translate(-camera.x, -camera.y, -camera.z)

        val consumers = context.consumers()

        val buffer = if (throughWalls) consumers?.getBuffer(FilledThroughWallsRenderLayer) else consumers?.getBuffer(RenderLayer.getDebugFilledBox())

        WorldRenderer.renderFilledBox(
            matrices, buffer, pos.x, pos.y, pos.z, pos.x + dimensions.x, pos.y + dimensions.y, pos.z + dimensions.z,
            color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), alpha)

        matrices.pop()
    }


    fun renderOutline(
        context: WorldRenderContext, pos: BlockPos, color: Color, lineWidth: Float, alpha: Float, throughWalls: Boolean
    ) {
        val box = blockPosToBox(pos)

        val matrices = context.matrixStack()
        val camera = context.camera().pos
        val tessellator = RenderSystem.renderThreadTesselator().buffer

        RenderSystem.setShader { GameRenderer.getRenderTypeLinesProgram() }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.lineWidth(lineWidth)
        RenderSystem.disableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.depthFunc(if (throughWalls) GL11.GL_ALWAYS else GL11.GL_LEQUAL)

        matrices.push()
        matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ())

        val buffer = tessellator.begin(DrawMode.LINES, VertexFormats.LINES)
        WorldRenderer.drawBox(
            matrices, tessellator, box,
            color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), alpha
        )
        BufferRenderer.drawWithGlobalProgram(tessellator.end())

        matrices.pop()
        RenderSystem.lineWidth(1f)
        RenderSystem.enableCull()
        RenderSystem.disableDepthTest()
        RenderSystem.depthFunc(GL11.GL_LEQUAL)

    }

    /**
     * Converts a BlockPos to a Box (aabb)
     *
     * @param pos BlockPos
     * @return a Box with min and max values
     */
    fun blockPosToBox(pos: BlockPos): Box {
        val minX = pos.x// - 0.5
        val minY = pos.y// - 0.5
        val minZ = pos.z// - 0.5
        val maxX = pos.x + 1
        val maxY = pos.y + 1
        val maxZ = pos.z + 1

        return Box(minX.toDouble(), minY.toDouble(), minZ.toDouble(), maxX.toDouble(), maxY.toDouble(), maxZ.toDouble())
    }

    fun blockPosToBoxCenter(pos: BlockPos): Box {
        val minX = pos.x - 0.5
        val minY = pos.y - 0.5
        val minZ = pos.z - 0.5
        val maxX = pos.x + 0.5
        val maxY = pos.y + 0.5
        val maxZ = pos.z + 0.5

        return Box(minX.toDouble(), minY.toDouble(), minZ.toDouble(), maxX.toDouble(), maxY.toDouble(), maxZ.toDouble())
    }

    /**
     * Converts a Box to a BlockPos
     *
     * @param box Box
     * @return a BlockPos with rounded values for x y z
     */
    fun boxToBlockPos(box: Box): BlockPos {
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
        val x = pos.x
        val y = pos.y
        val z = pos.z

        return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun blockPosToCenterVec(pos: BlockPos): Vec3d {
        val x = pos.x + 0.5
        val y = pos.y + 0.5
        val z = pos.z + 0.5

        return Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
    }

    /**
     * Convert a Vec3d to BlockPos. Rounds down to the nearest block coordinates.
     *
     * @param vec
     * @return BlockPos
     */
    fun vecToBlockPos(vec: Vec3d): BlockPos {
        val x = vec.x.toInt()
        val y = vec.y.toInt()
        val z = vec.z.toInt()

        return BlockPos(x, y, z)
    }

}