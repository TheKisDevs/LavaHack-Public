package com.kisman.cc.module.combat.autorer

import com.kisman.cc.module.combat.autorer.math.Vec3f
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHandSide
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

open class PlaceInfo(
    open var target: EntityLivingBase,
    open var blockPos: BlockPos,
    open var selfDamage: Float,
    open var targetDamage: Float,
    open var side: EnumFacing?,
    open var hitVecOffset: Vec3f?,
    open var hitVec: Vec3d?
) {
    open var mc: Minecraft = Minecraft.getMinecraft()

    class Mutable(
        target: EntityLivingBase,
        minDamage: Float
    ) : PlaceInfo(target, BlockPos.ORIGIN, Float.MAX_VALUE, minDamage, EnumFacing.UP, Vec3f.ZERO, Vec3d.ZERO) {
        inline fun update(
            target: EntityLivingBase,
            blockPos: BlockPos,
            selfDamage: Float,
            targetDamage: Float
        ) {
            this.target = target
            this.blockPos = blockPos
            this.selfDamage = selfDamage
            this.targetDamage = targetDamage
        }

        inline fun clear(player: EntityPlayerSP) {
            update(player, BlockPos.ORIGIN, Float.MAX_VALUE, targetDamage)
        }

/*        fun calcPlacement(event: SafeClientEvent) {
            event {
                when (placeBypass) {
                    PlaceBypass.UP -> {
                        side = EnumFacing.UP
                        hitVecOffset = Vec3f(0.5f, 1.0f, 0.5f)
                        hitVec = Vec3d(blockPos.x + 0.5, blockPos.y + 1.0, blockPos.z + 0.5)
                    }
                    PlaceBypass.DOWN -> {
                        side = EnumFacing.DOWN
                        hitVecOffset = Vec3f(0.5f, 0.0f, 0.5f)
                        hitVec = Vec3d(blockPos.x + 0.5, blockPos.y.toDouble(), blockPos.z + 0.5)
                    }
                    PlaceBypass.CLOSEST -> {
                        side = calcDirection(player.eyePosition, blockPos.toVec3dCenter())
                        val directionVec = side.directionVec
                        val x = directionVec.x * 0.5f + 0.5f
                        val y = directionVec.y * 0.5f + 0.5f
                        val z = directionVec.z * 0.5f + 0.5f
                        hitVecOffset = Vec3f(x, y, z)
                        hitVec = blockPos.toVec3dCenter(x.toDouble(), y.toDouble(), z.toDouble())
                    }
                }
            }
        }*/

        inline fun takeValid(damage: Float): Mutable? {
            return this.takeIf {
                target != mc.player
                        && selfDamage != Float.MAX_VALUE
                        && targetDamage != damage
            }
        }
    }

    companion object {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        @JvmField
        val INVALID = PlaceInfo(object : EntityLivingBase(null) {
            override fun getArmorInventoryList(): MutableIterable<ItemStack> {
                return ArrayList()
            }

            override fun setItemStackToSlot(slotIn: EntityEquipmentSlot, stack: ItemStack) {

            }

            override fun getItemStackFromSlot(slotIn: EntityEquipmentSlot): ItemStack {
                return ItemStack.EMPTY
            }

            override fun getPrimaryHand(): EnumHandSide {
                return EnumHandSide.RIGHT
            }
        }, BlockPos.ORIGIN, Float.NaN, Float.NaN, EnumFacing.UP, Vec3f.ZERO, Vec3d.ZERO)
    }
}