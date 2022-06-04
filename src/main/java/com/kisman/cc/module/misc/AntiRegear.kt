package com.kisman.cc.module.misc

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.BlockInteractionHelper
import com.kisman.cc.util.InventoryUtil
import com.kisman.cc.util.PlayerUtil
import i.gishreloaded.gishcode.utils.visual.ChatUtils
import net.minecraft.block.BlockShulkerBox
import net.minecraft.item.ItemPickaxe
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

/**
 * @author gerald0mc
 */
class AntiRegear : Module(
        "AntiRegear",
        "Breaks shulkers for you.",
        Category.MISC
) {
    val range = Setting("Range", this, 5.0, 0.0, 6.0, true)
    private val switchToPick = Setting("Switch To Pick", this, true)

    var oldSlot = -1
    private val breakQueue = ArrayList<BlockPos>()

    override fun update() {
        if(mc.player == null || mc.world == null) return
        if(breakQueue.isEmpty()) {
            if(oldSlot != -1 && mc.player.heldItemMainhand.item is ItemPickaxe && switchToPick.valBoolean) {
                ChatUtils.message("[AntiRegear] Switching back to original slot.")
                InventoryUtil.switchToSlot(oldSlot, false)
                oldSlot = -1
            }
        }
        for(pos in BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), range.valDouble.toFloat(), range.valInt, false, true, 0)) {
            if(mc.world.getBlockState(pos).block is BlockShulkerBox) {
                if (!breakQueue.contains(pos)) breakQueue.add(pos)
            }
        }
        if(breakQueue.isNotEmpty()) {
            if(mc.player.heldItemMainhand.item !is ItemPickaxe) {
                val pickSlot = InventoryUtil.findPickInHotbar()
                if(pickSlot != -1) {
                    oldSlot = mc.player.inventory.currentItem
                    InventoryUtil.switchToSlot(pickSlot, false)
                }
            }
            for(pos in breakQueue) {
                if(mc.world.getBlockState(pos).block !is BlockShulkerBox || mc.player.getDistance(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) > range.valInt) {
                    breakQueue.remove(pos)
                    return
                }
                mc.player.swingArm(EnumHand.MAIN_HAND)
                mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.getDirectionFromEntityLiving(pos, mc.player)))
                mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.getDirectionFromEntityLiving(pos, mc.player)))
            }
        }
    }
}