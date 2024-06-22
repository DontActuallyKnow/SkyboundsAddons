package com.tmiq.utils.render

import com.tmiq.mixin.accessors.FrustumInvoker
import com.tmiq.mixin.accessors.WorldRendererAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.Frustum
import net.minecraft.util.math.Box

object FrustumUtils {

    fun getFrustum(): Frustum {
        return (MinecraftClient.getInstance().worldRenderer as WorldRendererAccessor).frustum
    }

    fun isVisible(box: Box?): Boolean {
        return getFrustum().isVisible(box)
    }

    fun isVisible(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double): Boolean {
        return (getFrustum() as FrustumInvoker).invokeIsVisible(minX, minY, minZ, maxX, maxY, maxZ)
    }

}