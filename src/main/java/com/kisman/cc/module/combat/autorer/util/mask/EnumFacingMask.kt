package com.kisman.cc.module.combat.autorer.util.mask

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

object EnumFacingMask {
    const val DOWN = 1 shl 0
    const val UP = 1 shl 1
    const val NORTH = 1 shl 2
    const val SOUTH = 1 shl 3
    const val WEST = 1 shl 4
    const val EAST = 1 shl 5
    const val ALL = DOWN or UP or NORTH or SOUTH or WEST or EAST

    fun getMaskForSide(side: EnumFacing): Int {
        return when (side) {
            EnumFacing.DOWN -> DOWN
            EnumFacing.UP -> UP
            EnumFacing.NORTH -> NORTH
            EnumFacing.SOUTH -> SOUTH
            EnumFacing.WEST -> WEST
            EnumFacing.EAST -> EAST
        }
    }

    fun toAABB(aabb: AxisAlignedBB, side: EnumFacing): AxisAlignedBB {
        return when (side) {
            EnumFacing.DOWN -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY, aabb.maxZ)
            EnumFacing.UP -> AxisAlignedBB(aabb.minX, aabb.maxY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.NORTH -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.minZ)
            EnumFacing.SOUTH -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.maxZ, aabb.maxX, aabb.maxY, aabb.maxZ)
            EnumFacing.WEST -> AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ)
            EnumFacing.EAST -> AxisAlignedBB(aabb.maxX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)
        }
    }
}