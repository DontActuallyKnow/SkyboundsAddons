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
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
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
                renderFilled(context, BlockPos(0, 99, 0), ONE, Color(100, 0, 100), 0.5f, false);
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
        throughWalls: Boolean
    ) {
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
    }

    /* TODO
    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha, float xAxisRed, float yAxisGreen, float zAxisBlue) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        float f = (float) x1;
        float g = (float) y1;
        float h = (float) z1;
        float i = (float) x2;
        float j = (float) y2;
        float k = (float) z2;

        vertexConsumer.vertex(matrix4f, f, g, h).color(red, yAxisGreen, zAxisBlue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, yAxisGreen, zAxisBlue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, green, zAxisBlue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(xAxisRed, green, zAxisBlue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(xAxisRed, yAxisGreen, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(xAxisRed, yAxisGreen, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
    }
     */

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