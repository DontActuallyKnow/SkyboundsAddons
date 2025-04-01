package com.tmiq.utils.mc

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.atan2
import kotlin.math.sqrt

object EntityUtils {
    private val mc = MinecraftClient.getInstance()
    private val hiddenEntities = ConcurrentHashMap.newKeySet<Int>()

    // Get nearest entity of specific type
    fun <T : Entity> getNearestEntity(radius: Double, entityType: EntityType<T>): T? {
        val world = mc.world ?: return null
        val player = mc.player ?: return null
        return world.getEntitiesByType(entityType, Box.of(player.pos, radius, radius, radius)) { true }
            .minByOrNull { it.squaredDistanceTo(player) }
    }

    // Get all entities of specific type within radius
    fun <T : Entity> getEntitiesOfTypeInRadius(radius: Double, entityType: EntityType<T>): List<T> {
        val world = mc.world ?: return emptyList()
        val player = mc.player ?: return emptyList()
        return world.getEntitiesByType(entityType, Box.of(player.pos, radius, radius, radius)) { true }
    }

    // Get nearest armor stand with specific name
    fun getNearestArmorStandWithName(radius: Double, name: String): ArmorStandEntity? {
        val world = mc.world ?: return null
        val player = mc.player ?: return null
        return world.getEntitiesByType(EntityType.ARMOR_STAND, Box.of(player.pos, radius, radius, radius)) {
            it.hasCustomName() && it.customName?.string == name
        }.minByOrNull { it.squaredDistanceTo(player) }
    }

    // Get all armor stands with specific name
    fun getArmorStandsWithName(name: String, radius: Double): List<ArmorStandEntity> {
        val world = mc.world ?: return emptyList()
        val player = mc.player ?: return emptyList()
        return world.getEntitiesByType(EntityType.ARMOR_STAND, Box.of(player.pos, radius, radius, radius)) {
            it.hasCustomName() && it.customName?.string == name
        }
    }

    // Get positions of all armor stands with specific name
    fun getArmorStandPositions(name: String, radius: Double): List<Vec3d> {
        return getArmorStandsWithName(name, radius).map { it.pos }
    }

    // Get all entities in radius around player
    fun getEntitiesInRadius(radius: Double): List<Entity> {
        val world = mc.world ?: return emptyList()
        val player = mc.player ?: return emptyList()
        return world.getOtherEntities(player, Box.of(player.pos, radius, radius, radius)) { true }
    }

    // Get all entities in radius around a position
    fun getEntitiesInRadius(pos: Vec3d, radius: Double): List<Entity> {
        val world = mc.world ?: return emptyList()
        return world.getOtherEntities(null, Box.of(pos, radius, radius, radius)) { true }
    }

    // Get entities within player's field of view
    fun getEntitiesInFieldOfView(radius: Double, fovDegrees: Double): List<Entity> {
        val world = mc.world ?: return emptyList()
        val player = mc.player ?: return emptyList()
        val lookVec = player.rotationVector

        return getEntitiesInRadius(radius).filter { entity ->
            val directionVec = entity.pos.subtract(player.eyePos).normalize()
            val dotProduct = lookVec.dotProduct(directionVec)
            val angleCos = Math.cos(Math.toRadians(fovDegrees / 2))
            dotProduct > angleCos
        }
    }

    // Get entity player is looking at
    fun getTargetedEntity(maxDistance: Double): Entity? {
        val world = mc.world ?: return null
        val player = mc.player ?: return null
        val hit = mc.crosshairTarget

        return if (hit?.type == HitResult.Type.ENTITY) {
            val entityHit = hit as EntityHitResult
            if (player.pos.distanceTo(entityHit.entity.pos) <= maxDistance) {
                entityHit.entity
            } else null
        } else null
    }

    // Get distance between player and entity
    fun getDistanceToEntity(entity: Entity): Double {
        val player = mc.player ?: return Double.MAX_VALUE
        return player.pos.distanceTo(entity.pos)
    }

    // Get distance between entities
    fun getDistanceBetweenEntities(entity1: Entity, entity2: Entity): Double {
        return entity1.pos.distanceTo(entity2.pos)
    }

    // Get entity by ID
    fun getEntityById(id: Int): Entity? {
        val world = mc.world ?: return null
        return world.getEntityById(id)
    }

    // Hide entity (client-side only)
    fun hideEntity(entity: Entity) {
        hiddenEntities.add(entity.id)
    }

    // Show entity (cancel hiding)
    fun showEntity(entity: Entity) {
        hiddenEntities.remove(entity.id)
    }

    // Clear all hidden entities
    fun clearHiddenEntities() {
        hiddenEntities.clear()
    }

    // Check if entity is hidden
    fun isEntityHidden(entity: Entity): Boolean {
        return hiddenEntities.contains(entity.id)
    }

    // Get all hidden entities
    fun getHiddenEntities(): Set<Entity> {
        val world = mc.world ?: return emptySet()
        return hiddenEntities.mapNotNull { world.getEntityById(it) }.toSet()
    }

    // Filter visible entities from list (not hidden)
    fun filterVisibleEntities(entities: List<Entity>): List<Entity> {
        return entities.filter { !hiddenEntities.contains(it.id) }
    }

    // Get entities by name tag
    fun getEntitiesByNameTag(name: String, radius: Double): List<Entity> {
        val world = mc.world ?: return emptyList()
        val player = mc.player ?: return emptyList()
        return world.getOtherEntities(player, Box.of(player.pos, radius, radius, radius)) {
            it.hasCustomName() && it.customName?.string == name
        }
    }

    // Get nearest player (except self)
    fun getNearestPlayer(radius: Double): PlayerEntity? {
        val world = mc.world ?: return null
        val player = mc.player ?: return null
        return world.getEntitiesByType(EntityType.PLAYER, Box.of(player.pos, radius, radius, radius)) {
            it != player
        }.minByOrNull { it.squaredDistanceTo(player) }
    }

    // Calculate angle to entity (yaw)
    fun calculateYawToEntity(entity: Entity): Float {
        val player = mc.player ?: return 0f
        val deltaX = entity.x - player.x
        val deltaZ = entity.z - player.z
        return Math.toDegrees(atan2(deltaZ, deltaX)).toFloat() - 90f
    }

    // Calculate angle to entity (pitch)
    fun calculatePitchToEntity(entity: Entity): Float {
        val player = mc.player ?: return 0f
        val deltaX = entity.x - player.x
        val deltaY = entity.eyeY - player.eyeY
        val deltaZ = entity.z - player.z
        val horizontalDist = sqrt(deltaX * deltaX + deltaZ * deltaZ)
        return -Math.toDegrees(atan2(deltaY, horizontalDist)).toFloat()
    }

    // Get entities in a cone with specific angle and distance
    fun getEntitiesInCone(direction: Vec3d, maxDistance: Double, coneAngleDegrees: Double): List<Entity> {
        val world = mc.world ?: return emptyList()
        val player = mc.player ?: return emptyList()
        val entitiesInRadius = getEntitiesInRadius(maxDistance)
        val normalizedDirection = direction.normalize()

        return entitiesInRadius.filter { entity ->
            val entityDirection = entity.pos.subtract(player.pos).normalize()
            val dotProduct = normalizedDirection.dotProduct(entityDirection)
            val angle = Math.acos(dotProduct)
            angle <= Math.toRadians(coneAngleDegrees / 2)
        }
    }

    // Check if entity is behind player
    fun isEntityBehindPlayer(entity: Entity): Boolean {
        val player = mc.player ?: return false
        val lookVec = player.rotationVector
        val entityVec = entity.pos.subtract(player.pos).normalize()
        return lookVec.dotProduct(entityVec) < 0
    }

    // Get all living entities
    fun getLivingEntities(radius: Double): List<LivingEntity> {
        return getEntitiesInRadius(radius).filterIsInstance<LivingEntity>()
    }

    // Get entities sorted by distance
    fun getEntitiesSortedByDistance(radius: Double): List<Entity> {
        val player = mc.player ?: return emptyList()
        return getEntitiesInRadius(radius).sortedBy { it.squaredDistanceTo(player) }
    }

    // Check if entity is above block
    fun isEntityAboveBlock(entity: Entity): Boolean {
        val world = mc.world ?: return false
        val pos = entity.blockPos.down()
        return !world.getBlockState(pos).isAir
    }

    // Get entity position prediction (simple)
    fun predictEntityPosition(entity: Entity, ticksAhead: Int): Vec3d {
        return entity.pos.add(
            entity.velocity.x * ticksAhead,
            entity.velocity.y * ticksAhead,
            entity.velocity.z * ticksAhead
        )
    }

    // Get entities facing direction
    fun getEntityFacingDirection(entity: Entity): Direction {
        val yaw = entity.yaw
        return when {
            yaw < 45 || yaw > 315 -> Direction.SOUTH
            yaw < 135 -> Direction.WEST
            yaw < 225 -> Direction.NORTH
            else -> Direction.EAST
        }
    }

    // Check if entity is inside water
    fun isEntityInWater(entity: Entity): Boolean {
        return entity.isSubmergedInWater
    }

    // Get entity eye height
    fun getEntityEyeHeight(entity: Entity): Double {
        return entity.eyeY - entity.y
    }
}