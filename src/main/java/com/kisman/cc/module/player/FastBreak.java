package com.kisman.cc.module.player;

import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.*;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastBreak extends Module{
    public FastBreak() {
        super("FastBreak", "fast++", Category.PLAYER);
    }

    public void update() {
        PlayerControllerUtils.setBlockHitDelay(0);
    }

    @SubscribeEvent
	public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        float progress = PlayerControllerUtils.getCurBlockDamageMP() + BlockUtils.getHardness(event.getPos());	
    	if(progress >= 1) return;
    	mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), mc.objectMouseOver.sideHit));
	}
}
