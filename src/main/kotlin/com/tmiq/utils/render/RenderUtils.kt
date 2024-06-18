package com.tmiq.utils.render

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
import org.joml.Matrix4f

class RenderUtils {

    private val MAX_OVERWORLD_BUILD_HEIGHT = 319

    private val beaconList = mutableMapOf<BlockPos, FloatArray>()
    val colorCode = Utils().getColorCodeChar()

    fun init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register { context ->
            beaconList.forEach { (pos, colorComponents) ->
                renderBeaconBeam(context, pos, colorComponents)
            }
        }

        ServerWorldEvents.UNLOAD.register{ _, _ ->
            beaconList.clear()
        }

        WorldRenderEvents.AFTER_TRANSLUCENT.register { context ->
            val matrixStack = context.matrixStack()
            renderTextAtBlockPos(matrixStack, context.consumers(), BlockPos(0, 100, 0), Text.literal("${colorCode}4HELLO"), context.camera())
        }

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

    fun renderTextAtBlockPos(matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?, pos: BlockPos, text: Text, camera: Camera) {

        val client = MinecraftClient.getInstance()
        val d = camera.pos.squaredDistanceTo(Utils().blockPosToVec(pos));

        val rotation = camera.rotation

        matrixStack.push()
        matrixStack.translate(0.0, client.textRenderer.fontHeight.toDouble(), 0.0)
        matrixStack.multiply(rotation)

        matrixStack.scale(-0.025f, -0.025f, 0.025f)

        val matrix4f: Matrix4f = matrixStack.peek().positionMatrix

        val textRenderer: TextRenderer = client.textRenderer
        val x = (-textRenderer.getWidth(text) / 2).toFloat()

        textRenderer.draw(
            text.literalString,
            x,
            0f,
            553648127,
            false,
            matrix4f,
            vertexConsumerProvider,
            TextLayerType.SEE_THROUGH,
            -1,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

        matrixStack.pop()

        /*val client = MinecraftClient.getInstance()
        val camera = client.gameRenderer.camera
        val cameraPos = camera.pos

        val x = pos.x.toDouble() - cameraPos.x
        val y = pos.y.toDouble() - cameraPos.y
        val z = pos.z.toDouble() - cameraPos.z

        matrixStack.push()

        RenderSystem.disableCull()
        RenderSystem.disableBlend()

        matrixStack.translate(x, y, z)
        matrixStack.scale(-0.025f, -0.025f, 0.025f)

        val textRenderer = client.textRenderer
        val matrix4f = matrixStack.peek().positionMatrix

        textRenderer.draw(text, 0f, 0f, 0xFFFFFF, false, matrix4f, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE)

        RenderSystem.enableBlend()
        RenderSystem.enableCull()

        matrixStack.pop()*/

    }

    /*
    protected void renderLabelIfPresent(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double d = this.dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        matrixStack.push();
        if (d < 100.0) {
            Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            if (scoreboardObjective != null) {
                ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(abstractClientPlayerEntity, scoreboardObjective);
                Text text2 = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreboardObjective.getNumberFormatOr(StyledNumberFormat.EMPTY));
                super.renderLabelIfPresent(abstractClientPlayerEntity, Text.empty().append(text2).append(ScreenTexts.SPACE).append(scoreboardObjective.getDisplayName()), matrixStack, vertexConsumerProvider, i);
                Objects.requireNonNull(this.getTextRenderer());
                matrixStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
            }
        }

        super.renderLabelIfPresent(abstractClientPlayerEntity, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
     */

}