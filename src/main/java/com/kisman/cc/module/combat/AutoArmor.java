 package com.kisman.cc.module.combat;

 import com.kisman.cc.Kisman;
 import com.kisman.cc.module.*;
 import com.kisman.cc.gui.csgo.components.Slider;
 import com.kisman.cc.util.InventoryUtil;
 import com.kisman.cc.settings.*;

 import i.gishreloaded.gishcode.utils.TimerUtils;
 import net.minecraft.client.gui.inventory.GuiContainer;
 import net.minecraft.client.renderer.InventoryEffectRenderer;
 import net.minecraft.enchantment.*;
 import net.minecraft.init.Items;
 import net.minecraft.inventory.ClickType;
 import net.minecraft.item.*;

 import java.util.*;

 public class AutoArmor extends Module {
     private final Setting delay = new Setting("Delay", this, 0, 0, 100, Slider.NumberType.TIME);

     public static AutoArmor instance;
     private final TimerUtils timer = new TimerUtils();

     public AutoArmor() {
         super("AutoArmor", "ebate srate lox!", Category.COMBAT);

         instance = this;

         setmgr.rSetting(delay);
         Kisman.instance.settingsManager.rSetting(new Setting("NoThorns", this, false));
     }

     public void onEnable() {
         timer.reset();
     }

     public void update() {
         if(mc.player == null || mc.world == null) return;
         super.setDisplayInfo("[" + delay.getValInt() + "]");
         if(!timer.passedMillis(delay.getValLong())) return; else timer.reset();
         if (mc.player.ticksExisted % 2 == 0) return;
         if (mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof InventoryEffectRenderer)) return;

         boolean noThorns = Kisman.instance.settingsManager.getSettingByName(this, "NoThorns").getValBoolean();

         List<ItemStack> armorInventory = mc.player.inventory.armorInventory;
         List<ItemStack> inventory = mc.player.inventory.mainInventory;

         int[] bestArmorSlots = {-1, -1, -1, -1};
         int[] bestArmorValues = {-1, -1, -1, -1};

         for (int i = 0; i < 4; i++) {
             ItemStack oldArmour = armorInventory.get(i);
             if (oldArmour.getItem() instanceof ItemArmor) bestArmorValues[i] = ((ItemArmor) oldArmour.getItem()).damageReduceAmount;
         }

         List<Integer> slots = InventoryUtil.findAllItemSlots(ItemArmor.class);
         HashMap<Integer, ItemStack> armour = new HashMap<>();
         HashMap<Integer, ItemStack> thorns = new HashMap<>();

         for (Integer slot : slots) {
             ItemStack item = inventory.get(slot);
             if (noThorns && EnchantmentHelper.getEnchantments(item).containsKey(Enchantment.getEnchantmentByID(7))) thorns.put(slot, item);
             else armour.put(slot, item);
         }

         armour.forEach(((integer, itemStack) -> {
             ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
             int armorType = itemArmor.armorType.ordinal() - 2;
             if (armorType == 2 && mc.player.inventory.armorItemInSlot(armorType).getItem().equals(Items.ELYTRA)) return;
             int armorValue = itemArmor.damageReduceAmount;
             if (armorValue > bestArmorValues[armorType]) {
                 bestArmorSlots[armorType] = integer;
                 bestArmorValues[armorType] = armorValue;
             }
         }));

         if (noThorns) {
             thorns.forEach(((integer, itemStack) -> {
                 ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
                 int armorType = itemArmor.armorType.ordinal() - 2;

                 if (!(armorInventory.get(armorType) == ItemStack.EMPTY && bestArmorSlots[armorType] == -1)) return;
                 if (armorType == 2 && mc.player.inventory.armorItemInSlot(armorType).getItem().equals(Items.ELYTRA)) return;
                 int armorValue = itemArmor.damageReduceAmount;
                 if (armorValue > bestArmorValues[armorType]) {
                     bestArmorSlots[armorType] = integer;
                     bestArmorValues[armorType] = armorValue;
                 }
             }));
         }

         for (int i = 0; i < 4; i++) {
             int slot = bestArmorSlots[i];
             if (slot == -1) continue;
             if (slot < 9) slot += 36;
             mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
             mc.playerController.windowClick(0, 8 - i, 0, ClickType.PICKUP, mc.player);
             mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
         }
     }
 }
