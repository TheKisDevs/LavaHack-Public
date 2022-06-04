package com.kisman.cc.module.combat;

import com.google.common.collect.Sets;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Work in progress
 * @author Cubic
 */
public class HoleFillerRewrite extends Module {

    private final Setting obsidianHoles = register(new Setting("ObsidianHoles", this, true));
    private final Setting bedrockHoles = register(new Setting("BedrockHoles", this, true));
    private final Setting singleHoles = register(new Setting("SingleHoles", this, true));
    private final Setting doubleHoles = register(new Setting("DoubleHoles", this, true));
    private final Setting customHoles = register(new Setting("CustomHoles", this, true));
    private final Setting blocks = register(new Setting("Blocks", this, "Obsidian", Arrays.asList("Obsidian", "EnderChest")));
    private final Setting swap = register(new Setting("Switch", this, "Silent", Arrays.asList("None", "Vanilla", "Normal", "Packet", "Silent")));
    private final Setting rotate = register(new Setting("Rotate", this, false));
    private final Setting packet = register(new Setting("Packet", this, false));
    private final Setting place = register(new Setting("Place", this, "Instant", Arrays.asList("Instant", "Tick", "Delay")));
    private final Setting delay = register(new Setting("DelayMS", this, 50, 0, 500, true).setVisible(() -> place.getValString().equals("Delay")));
    private final Setting placeMode = register(new Setting("PlaceMode", this, "All", Arrays.asList("All", "Target")));
    private final Setting enemyRange = register(new Setting("TargetRange", this, 10, 1, 15, false).setVisible(() -> placeMode.getValString().equals("Target")));
    private final Setting aroundEnemyRange = register(new Setting("TargetHoleRange", this, 4, 1, 10, false).setVisible(() -> placeMode.getValString().equals("Target")));
    private final Setting holeRange = register(new Setting("HoleRange", this, 5, 1, 10, false));
    private final Setting limit = register(new Setting("Limit", this, 0, 0, 100, true));

    public HoleFillerRewrite(){
        super("HoleFillerRewrite", Category.COMBAT);
    }

    private final Set<AxisAlignedBB> holes = Sets.newConcurrentHashSet();

    private int getBlockSlot(){
        if(blocks.getValString().equals("Obsidian")){
            return InventoryUtil.getBlockInHotbar(Blocks.OBSIDIAN);
        } else {
            return InventoryUtil.getBlockInHotbar(Blocks.ENDER_CHEST);
        }
    }

    private void place(BlockPos pos, int slot){
        int oldSlot = mc.player.inventory.currentItem;
        doSwitch(slot, false);
        BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, rotate.getValBoolean(), packet.getValBoolean(), false);
        doSwitch(oldSlot, true);
    }

    private void doSwitch(int slot, boolean swapBack){
        switch(swap.getValString()){
            case "None":
                break;
            case "Vanilla":
                if(swapBack)
                    break;
                mc.player.inventory.currentItem = slot;
                break;
            case "Normal":
                mc.player.inventory.currentItem = slot;
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                break;
            case "Silent":
                mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                mc.player.inventory.currentItem = slot;
                break;
        }
    }
}
