package com.kisman.cc.catlua.util;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.HashMap;

public class PacketUtil {
    public static final HashMap<String, Class<? extends Packet<?>>> cache = new HashMap<>();

    static {
        cache.put("CPacketAnimation", CPacketAnimation.class);
        cache.put("CPacketChatMessage", CPacketChatMessage.class);
        cache.put("CPacketClickWindow", CPacketClickWindow.class);
        cache.put("CPacketClientSettings", CPacketClientSettings.class);
        cache.put("CPacketClientStatus", CPacketClientStatus.class);
        cache.put("CPacketCloseWindow", CPacketCloseWindow.class);
        cache.put("CPacketConfirmTeleport", CPacketConfirmTeleport.class);
        cache.put("CPacketConfirmTransaction", CPacketConfirmTransaction.class);
        cache.put("CPacketCreativeInventoryAction", CPacketCreativeInventoryAction.class);
        cache.put("CPacketCustomPayload", CPacketCustomPayload.class);
        cache.put("CPacketEnchantItem", CPacketEnchantItem.class);
        cache.put("CPacketEntityAction", CPacketEntityAction.class);
        cache.put("CPacketHeldItemChange", CPacketHeldItemChange.class);
        cache.put("CPacketInput", CPacketInput.class);
        cache.put("CPacketKeepAlive", CPacketKeepAlive.class);
        cache.put("CPacketPlaceRecipe", CPacketPlaceRecipe.class);
        cache.put("CPacketPlayer", CPacketPlayer.class);
        cache.put("CPacketPlayerAbilities", CPacketPlayerAbilities.class);
        cache.put("CPacketPlayerDigging", CPacketPlayerDigging.class);
        cache.put("CPacketPlayerTryUseItem", CPacketPlayerTryUseItem.class);
        cache.put("CPacketPlayerTryUseItemOnBlock", CPacketPlayerTryUseItemOnBlock.class);
        cache.put("CPacketRecipeInfo", CPacketPlaceRecipe.class);
        cache.put("CPacketResourcePackStatus", CPacketResourcePackStatus.class);
        cache.put("CPacketSeenAdvancements", CPacketSeenAdvancements.class);
        cache.put("CPacketSpectate", CPacketSpectate.class);
        cache.put("CPacketSteerBoat", CPacketSteerBoat.class);
        cache.put("CPacketTabComplete", CPacketTabComplete.class);
        cache.put("CPacketUpdateSign", CPacketUpdateSign.class);
        cache.put("CPacketUseEntity", CPacketUseEntity.class);
        cache.put("CPacketVehicleMove", CPacketVehicleMove.class);
        cache.put("CPacketPlayer.Position", CPacketPlayer.Position.class);
        cache.put("CPacketPlayer.PositionRotation", CPacketPlayer.PositionRotation.class);
        cache.put("CPacketPlayer.Rotation", CPacketPlayer.Rotation.class);
    }
}
