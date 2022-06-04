package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.*;
import net.minecraft.util.EnumHand;

import java.util.Objects;

public class AutoPot extends Module {
    private TimerUtils timer = new TimerUtils();
    private ItemStack stack = null;

    public AutoPot() {
        super("AutoPot", "auto potion", Category.COMBAT);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        if(stack == null && timer.hasReached(100)) stack = mc.player.getHeldItemMainhand();
        if (isPotionOnHotBar() && mc.player.onGround) {
            float oldPitch = mc.player.rotationPitch;
            mc.player.rotationPitch = 90;

            if (!mc.player.isPotionActive((Objects.requireNonNull(Potion.getPotionById(1))))) throwPot(Potions.SPEED);
            if (!mc.player.isPotionActive((Objects.requireNonNull(Potion.getPotionById(5))))) throwPot(Potions.STRENGTH);
            if (!mc.player.isPotionActive((Objects.requireNonNull(Potion.getPotionById(12))))) throwPot(Potions.FIRERES);

            mc.player.rotationPitch = oldPitch;
            timer.reset();
        }
    }

    public boolean isBeta() {return true;}

    private void throwPot(Potions potion) {
        int slot = getPotionSlot(potion);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.playerController.updateController();
        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
    }

    private int getPotionSlot(Potions potion) {
        for(int i = 0; i < 9; ++i) if (this.isStackPotion(mc.player.inventory.getStackInSlot(i), potion)) return i;
        return -1;
    }

    private boolean isPotionOnHotBar() {
        for(int i = 0; i < 9; ++i) if(isStackPotion(mc.player.inventory.getStackInSlot(i), Potions.STRENGTH) || isStackPotion(mc.player.inventory.getStackInSlot(i), Potions.SPEED)|| isStackPotion(mc.player.inventory.getStackInSlot(i), Potions.FIRERES)) return true;
        return false;
    }

    private boolean isStackPotion(ItemStack stack, Potions potion){
        if(stack == null) return false;

        Item item = stack.getItem();

        if(item == Items.SPLASH_POTION){
            int id = 5;

            switch (potion){
                case STRENGTH:
                    id = 5;
                    break;
                case SPEED:
                    id = 1;
                    break;
                case FIRERES:
                    id = 12;
                    break;
            }

            for(PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) if(effect.getPotion() == Potion.getPotionById(id)) return true;
        }

        return false;
    }
    enum Potions {
        STRENGTH,
        SPEED,
        FIRERES
    }
}
