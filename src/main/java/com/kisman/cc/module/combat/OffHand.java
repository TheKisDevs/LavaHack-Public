package com.kisman.cc.module.combat;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.gui.ChatFormatting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import org.lwjgl.input.Mouse;

import java.util.*;

public class OffHand extends Module {
    public static OffHand instance;

    private final Setting health = new Setting("Health", this, 11, 0, 20, true);

    private final Setting mode = new Setting("Mode", this, "Totem", new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple", "Pearl", "Chorus", "Strength", "Shield")));
    private final Setting fallBackMode = new Setting("FallBackMode", this, "Crystal", new ArrayList<>(Arrays.asList("Totem", "Crystal", "Gapple", "Pearl", "Chorus", "Strength", "Shield")));
    private final Setting fallBackDistance = new Setting("FallBackDistance", this, 15, 0, 100, true);
    private final Setting totemOnElytra = new Setting("TotemOnElytra", this, true);
    private final Setting offhandGapOnSword = new Setting("GapOnSword", this, true);
    private final Setting rightClickGap = new Setting("Right Click Gap", this, false);
    private final Setting hotbarFirst = new Setting("HotbarFirst", this, false);
    private final Setting useUpdateController = new Setting("Use UpdateController", this, true);
    private final Setting antiTotemFail = new Setting("Anti Totem Fail", this, true);
    private final Setting terrain = new Setting("Terrain", this, true);

    public OffHand() {
        super("OffHand", "gg", Category.COMBAT);

        instance = this;

        setmgr.rSetting(health);
        setmgr.rSetting(mode);
        setmgr.rSetting(fallBackMode);
        setmgr.rSetting(fallBackDistance);
        setmgr.rSetting(totemOnElytra);
        setmgr.rSetting(offhandGapOnSword);
        setmgr.rSetting(hotbarFirst);
        setmgr.rSetting(useUpdateController);
        setmgr.rSetting(antiTotemFail);
        setmgr.rSetting(terrain);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;
        if (mc.currentScreen != null && (!(mc.currentScreen instanceof GuiInventory))) return;

        super.setDisplayInfo("[" + mode.getValString() + "]");

        if(antiTotemFail.getValBoolean() && canTotemFail()) {
            switchOffHandIfNeed("Totem");
            return;
        }

        if (!mc.player.getHeldItemMainhand().isEmpty()) {
            if (health.getValDouble() <= (mc.player.getHealth() + mc.player.getAbsorptionAmount()) && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && offhandGapOnSword.getValBoolean()) {
                switchOffHandIfNeed("Gap");
                return;
            }
        }

        if (health.getValDouble() > (mc.player.getHealth() + mc.player.getAbsorptionAmount()) || mode.getValString().equalsIgnoreCase("Totem") || (totemOnElytra.getValBoolean() && mc.player.isElytraFlying()) || (mc.player.fallDistance >= fallBackDistance.getValDouble() && !mc.player.isElytraFlying()) || noNearbyPlayers()) {
            switchOffHandIfNeed("Totem");
            return;
        }

        if(rightClickGap.getValBoolean() && Mouse.isButtonDown(1) && !mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) && !mc.player.getHeldItemOffhand().getItem().equals(Items.GOLDEN_APPLE)) {
            switchOffHandIfNeed("Gap");
            return;
        }
        switchOffHandIfNeed(mode.getValString());
    }

    private boolean canTotemFail() {
        try {
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) && !mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
                        double selfDamage = CrystalUtils.calculateDamage(mc.world, crystal.posX + 0.5, crystal.posY, crystal.posZ + 0.5, mc.player, terrain.getValBoolean());
                        if (selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void switchOffHandIfNeed(String mode) {
        Item item = getItemFromModeVal(mode);

        if (mc.player.getHeldItemOffhand().getItem() != item) {
            int slot = hotbarFirst.getValBoolean() ? PlayerUtil.GetRecursiveItemSlot(item) : PlayerUtil.GetItemSlot(item);

            Item fallback = getItemFromModeVal(fallBackMode.getValString());

            String display = getItemNameFromModeVal(mode);

            if (slot == -1 && item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                slot = PlayerUtil.GetRecursiveItemSlot(fallback);
                display = getItemNameFromModeVal(fallBackMode.getValString());

                if (slot == -1 && fallback != Items.TOTEM_OF_UNDYING) {
                    fallback = Items.TOTEM_OF_UNDYING;

                    if (item != fallback && mc.player.getHeldItemOffhand().getItem() != fallback) {
                        slot = PlayerUtil.GetRecursiveItemSlot(fallback);
                        display = "Emergency Totem";
                    }
                }
            }

            if (slot != -1) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
                if(useUpdateController.getValBoolean()) mc.playerController.updateController();

                ChatUtils.complete(ChatFormatting.BLUE + "Offhand now has a " + display);
            }
        }
    }

    private boolean isValidTarget(EntityPlayer player) {
        if (player == mc.player) return false;
        if (mc.player.getDistance(player) > 15) return false;
        return !FriendManager.instance.isFriend(player);
    }

    public Item getItemFromModeVal(String mode) {
        switch (mode) {
            case "Crystal": return Items.END_CRYSTAL;
            case "Gap": return Items.GOLDEN_APPLE;
            case "Pearl": return Items.ENDER_PEARL;
            case "Chorus": return Items.CHORUS_FRUIT;
            case "Strength": return Items.POTIONITEM;
            case "Shield": return Items.SHIELD;
            default: return Items.TOTEM_OF_UNDYING;
        }
    }

    private String getItemNameFromModeVal(String mode) {
        switch (mode) {
            case "Crystal": return "End Crystal";
            case "Gap": return "Gap";
            case "Pearl": return "Pearl";
            case "Chorus": return "Chorus";
            case "Strength": return "Strength";
            case "Shield": return "Shield";
            default: return "Totem";
        }
    }

    private boolean noNearbyPlayers() {return mode.getValString().equalsIgnoreCase("Crystal") && mc.world.playerEntities.stream().noneMatch(e -> e != mc.player && isValidTarget(e));}
}