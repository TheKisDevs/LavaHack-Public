package com.kisman.cc.util;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int findItemInInventory(Item item) {
        if(mc.player != null) {
            for (int i = mc.player.inventoryContainer.getInventory().size() - 1; i > 0; --i) {
                if (i == 5 || i == 6 || i == 7 || i == 8) continue;

                ItemStack s = mc.player.inventoryContainer.getInventory().get(i);

                if (s.isEmpty()) continue;
                if (s.getItem() == item) return i;
            }
        } return -1;
    }

    public long time(BlockPos pos) {
        return time(pos, EnumHand.MAIN_HAND);
    }

    public long time(BlockPos pos, EnumHand hand) {
        return time(pos, mc.player.getHeldItem(hand));
    }

    public static long time(BlockPos pos, ItemStack stack) {
        final IBlockState state = mc.world.getBlockState(pos);
        final Block block = state.getBlock();
        float toolMultiplier = stack.getDestroySpeed(state);

        toolMultiplier += Math.pow(EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack), 2) + 1;

        if (mc.player.isPotionActive(MobEffects.HASTE)) toolMultiplier *= 1.0F + ( float ) (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f1;
            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
                    break;
            }
            toolMultiplier *= f1;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            toolMultiplier /= 5.0F;
        }

        float dmg = toolMultiplier / state.getBlockHardness(mc.world, pos);

        if (canHarvestBlock(block, pos, stack) || block == Blocks.ENDER_CHEST) dmg /= 30;
        else dmg /= 100;
        float ticks = ( float ) (Math.floor(1.0f / dmg) + 1.0f);

        return ( long ) ((ticks / 20.0f) * 1000);
    }

    public static boolean canHarvestBlock(Block block, BlockPos pos, ItemStack stack) {
        IBlockState state = mc.world.getBlockState(pos);
        state = state.getBlock().getActualState(state, mc.world, pos);
        if (state.getMaterial().isToolNotRequired()) return true;
        String tool = block.getHarvestTool(state);
        if (stack.isEmpty() || tool == null) return mc.player.canHarvestBlock(state);
        final int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);
        if (toolLevel < 0) return mc.player.canHarvestBlock(state);
        return toolLevel >= block.getHarvestLevel(state);
    }

    public static int findBestToolSlot(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        int bestSlot = 0;
        double bestSpeed = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == Items.AIR) continue;
            float speed = stack.getDestroySpeed(state);
            int eff;
            if (speed > 0) {
                speed += ((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0 ? (Math.pow(eff, 2) + 1) : 0);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    public static void switchToSlot(int slot, boolean silent) {
        if(slot == -1) return;
        if (!silent) mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
        }
    }

    public static int findWeaponSlot(int min, int max, boolean shieldBreak) {
        for(int i = min; i <= max; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if(shieldBreak) if(stack.getItem() instanceof ItemAxe) return i;
            else if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe) return i;
        }

        return -1;
    }

    public static boolean isArmorLow(final EntityPlayer player, final int durability) {
        for (int i = 0; i < 4; ++i) if (getDamageInPercent(player.inventory.armorInventory.get(i)) < durability) return true;

        return false;
    }

    public static float getDamageInPercent(final ItemStack stack) {
        final float green = (stack.getMaxDamage() - ( float ) stack.getItemDamage()) / stack.getMaxDamage();
        final float red = 1.0f - green;
        return ( float ) (100 - ( int ) (red * 100.0f));
    }

    public static float getDamageInFloat(ItemStack stack) {
        return 1 - ((stack.getMaxDamage() - ( float ) stack.getItemDamage()) / stack.getMaxDamage());
    }

    public static int findItem(Item item, int min, int max) {
        for(int i = min; i <= max; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            return i;
        }

        return -1;
    }

    public static int findChestplate(int min, int max) {
        for(int i = min; i <= max; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.CHAINMAIL_CHESTPLATE && stack.getItem() == Items.DIAMOND_CHESTPLATE && stack.getItem() == Items.IRON_CHESTPLATE && stack.getItem() == Items.GOLDEN_CHESTPLATE && stack.getItem() == Items.LEATHER_CHESTPLATE) return i;
        }

        return -1;
    }

    public static int findAntiWeaknessTool() {
        return findAntiWeaknessTool(0, 9);
    }

    public static int findAntiWeaknessTool(int min, int max) {
        for(int i = min; i <= max; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemPickaxe) return i;
        }

        return -1;
    }

    public static int findBlock(Block block, int min, int max) {
        for (int i = min; i <= max; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!(stack.getItem() instanceof ItemBlock)) continue;
            ItemBlock item = (ItemBlock)stack.getItem();
            if (item.getBlock() != block) continue;
            return i;
        }
        return -1;
    }

    public static void switchToSlot(int slot, Switch switchMode) {
        if(mc.player == null) return;

        if (slot != -1 && mc.player.inventory.currentItem != slot) {
            switch (switchMode) {
                case NORMAL:
                    mc.player.inventory.currentItem = slot;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    break;
                case PACKET:
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                    break;
            }
        }

        mc.playerController.updateController();
//        ((IPlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }

    public static void switchToSlot(Item item, Switch switchMode) {
        if (getItemSlot(item, Inventory.HOTBAR, true) != -1 && mc.player.inventory.currentItem != getItemSlot(item, Inventory.HOTBAR, true))
            switchToSlot(getItemSlot(item, Inventory.HOTBAR, true), switchMode);

//        ((IPlayerControllerMP) mc.playerController).syncCurrentPlayItem();
    }

    public static void switchToSlotGhost(final int slot) {
        if (slot != -1 && InventoryUtil.mc.player.inventory.currentItem != slot) {
            InventoryUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(slot));
        }
    }

    public static void switchToSlotGhost(final Block block) {
        if (getBlockInHotbar(block) != -1 && InventoryUtil.mc.player.inventory.currentItem != getBlockInHotbar(block)) {
            InventoryUtil.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(getBlockInHotbar(block)));
        }
    }

    public static void switchToSlotGhost(final Item item) {
        if (getHotbarItemSlot(item) != -1 && InventoryUtil.mc.player.inventory.currentItem != getHotbarItemSlot(item)) {
            switchToSlotGhost(getHotbarItemSlot(item));
        }
    }

    public static int getHotbarItemSlot(final Item item) {
        for (int i = 0; i < 9; ++i) {
            if (InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static int getBlockInHotbar(final Block block) {
        for (int i = 0; i < 9; ++i) {
            final Item item = InventoryUtil.mc.player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemBlock && ((ItemBlock)item).getBlock().equals(block)) {
                return i;
            }
        }
        return -1;
    }

    public static int getBlockInHotbar(boolean onlyObby) {
        for(int i = 0; i <9; i++) if(mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) return i;
        return -1;
    }

    public static int getItemSlot(Item item, Inventory inventory, boolean hotbar) {
        switch (inventory) {
            case HOTBAR:
                for (int i = 0; i < 9; i++) if (mc.player.inventory.getStackInSlot(i).getItem() == item) return i;
                break;
            case INVENTORY:
                for (int i = hotbar ? 9 : 0; i < 45; i++) if (mc.player.inventory.getStackInSlot(i).getItem() == item) return i;
                break;
        }

        return -1;
    }

    public static int findItemInHotbar(Class<? extends Item> itemToFind) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for(int i = 0; i < 9; i++) {
            ItemStack stack = mainInventory.get(i);

            if(stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;
            if(itemToFind.isInstance(stack.getItem())) slot = i;
        }

        return slot;
    }

    public static int findPickInHotbar() {
        return findItemInHotbar(ItemPickaxe.class);
    }

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;
            if (itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static List<Integer> findAllItemSlots(Class<? extends Item> itemToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;
            slots.add(i);
        }
        return slots;
    }

    public static List<Integer> findAllItemSlots(Item itemToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            if (itemToFind != mainInventory.get(i).item) continue;
            slots.add(i);
        }
        return slots;
    }

    public static List<Integer> findAllBlockSlots(Class<? extends Block> blockToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slots.add(i);
            }
        }
        return slots;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();

        result = isInstanceOf(stack, clazz);

        return result;
    }

    //rerhack
    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (int i = 3; i >= 0; --i) {
            final ItemStack stack = player.inventory.armorInventory.get(i);
            if (getDamageInPercent(stack) < percent) return true;
        }
        return false;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }

    //zero two
    public static
    boolean isInstanceOf ( ItemStack stack , Class clazz ) {
        if ( stack == null ) {
            return false;
        }

        Item item = stack.getItem ( );
        if ( clazz.isInstance ( item ) ) {
            return true;
        }

        if ( item instanceof ItemBlock ) {
            Block block = Block.getBlockFromItem ( item );
            return clazz.isInstance ( block );
        }

        return false;
    }

    public enum Switch {
        NORMAL, PACKET, NONE
    }

    public enum Inventory {
        INVENTORY, HOTBAR, CRAFTING
    }
}
