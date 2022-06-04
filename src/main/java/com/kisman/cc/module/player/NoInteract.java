package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.*;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

public class NoInteract extends Module {
    private Setting enderChest = new Setting("EnderChest", this, true);
    private Setting craft = new Setting("CraftingTable", this, true);
    private Setting chest = new Setting("Chest", this, true);
    private Setting furnace = new Setting("Furnace", this, true);
    private Setting armorStand = new Setting("ArmorStand", this, true);
    private Setting anvil = new Setting("Anvil", this, true);

    public NoInteract() {
        super("NoInteract", "NoInteract", Category.PLAYER);

        setmgr.rSetting(enderChest);
        setmgr.rSetting(craft);
        setmgr.rSetting(chest);
        setmgr.rSetting(furnace);
        setmgr.rSetting(armorStand);
        setmgr.rSetting(anvil);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(sendListener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(sendListener);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<>(event -> {
        if(mc.player == null && mc.world == null) return;

        if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.objectMouseOver != null &&mc.objectMouseOver.getBlockPos() != null && mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() != null) {

            final Block block = mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

            if ((block == Blocks.CRAFTING_TABLE && craft.getValBoolean()) ||
                    (block == Blocks.FURNACE && furnace.getValBoolean()) ||
                    (block == Blocks.ENDER_CHEST && enderChest.getValBoolean()) ||
                    (block == Blocks.CHEST && chest.getValBoolean()) ||
                    (block == Blocks.ANVIL && anvil.getValBoolean()) ||
                    (mc.objectMouseOver.entityHit instanceof EntityArmorStand && armorStand.getValBoolean())) {
                event.cancel();
            }
        }
    });
}
