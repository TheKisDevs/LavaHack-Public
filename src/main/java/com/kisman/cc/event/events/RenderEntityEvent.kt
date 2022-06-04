package com.kisman.cc.event.events

import com.kisman.cc.event.Event
import net.minecraft.client.renderer.entity.Render
import net.minecraft.entity.Entity

sealed class RenderEntityEvent(
    val entity: Entity
) : Event() {
    sealed class All(
        entity: Entity,
        private val x: Double,
        private val y: Double,
        private val z: Double,
        private val yaw: Float,
        private val partialTicks: Float
    ) : RenderEntityEvent(entity) {

        class Pre(
            entity: Entity,
            x: Double,
            y: Double,
            z: Double,
            yaw: Float,
            partialTicks: Float
        ) : All(entity, x, y, z, yaw, partialTicks)

        class Post(
            entity: Entity,
            x: Double,
            y: Double,
            z: Double,
            yaw: Float,
            partialTicks: Float
        ) : All(entity, x, y, z, yaw, partialTicks)
    }

    companion object {
        @JvmStatic
        var renderingEntities = false
    }
}