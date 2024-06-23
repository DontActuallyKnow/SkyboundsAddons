package com.tmiq.utils.render

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.awt.Color

object WaypointHandler {

    private val waypoints = mutableListOf<Waypoint>()

    fun init () {
        WorldRenderEvents.AFTER_TRANSLUCENT.register { context ->
            for (waypoint in waypoints) {
                RenderUtils.renderBoxWithBeam(context, waypoint.pos, waypoint.color, waypoint.alpha, waypoint.throughWalls, true)
                if (waypoint.displayDistance) {
                    RenderUtils.renderWaypointText(context, waypoint.title, waypoint.getVec3d(), waypoint.scale, 0f,
                        true, waypoint.displayDistance, waypoint.maxDistanceScaling, waypoint.distanceColor)
                }
            }
        }

        ServerWorldEvents.LOAD.register { _, _ ->
            waypoints.clear()
        }

    }

    fun addWaypoint(waypoint: Waypoint) {
        if(!waypoints.contains(waypoint)) waypoints.add(waypoint)
    }

    fun removeWaypoint(waypoint: Waypoint) {
        if(waypoints.contains(waypoint)) waypoints.remove(waypoint)
    }

    fun clearWaypoints() {
        waypoints.clear()
    }

}

data class Waypoint (
    var title: Text,
    var pos: BlockPos,
    var color: Color,
    var alpha: Float,
    var throughWalls: Boolean,
    var displayDistance: Boolean,
    var distanceColor: Char,
    var maxDistanceScaling: Double,
    var scale: Float
) {

    fun getVec3d(): Vec3d {
        val blockPos = this.pos.add(0, 1, 0)
        return RenderUtils.blockPosToCenterVec(blockPos)
    }

}