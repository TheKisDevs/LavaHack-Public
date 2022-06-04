package com.kisman.cc.module.misc;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;

public class MurderFinder extends Module {
    public MurderFinder() {
        super("MurderFinder", "MurderFinder", Category.MISC);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer).filter(entity -> entity == mc.player).forEach(entity -> {
            if(isMurderer((EntityPlayer) entity)) entity.setGlowing(true);
        });
    }

    public void onDisable() {
        if(mc.player == null && mc.world == null) return;

        mc.world.loadedEntityList.stream().filter(entity -> entity.isGlowing()).filter(entity -> entity != mc.player).forEach(entity -> { entity.setGlowing(false); });
    }

    public static boolean isMurderer(final EntityPlayer player) {
        for (int i = 0; i < 9; ++i) {
            if (player.inventory.getStackInSlot(i) != null && (player.inventory.getStackInSlot(i).getItem() instanceof ItemSword) && !(player.inventory.getStackInSlot(i).getItem() instanceof ItemBow) && !(player.inventory.getStackInSlot(i).getItem() instanceof ItemMap) && !(player.inventory.getStackInSlot(i).getItem() instanceof ItemPotion) && !(player.inventory.getStackInSlot(i).getItem() instanceof ItemEmptyMap) && !(player.inventory.getStackInSlot(i).getItem() instanceof ItemSnowball) && player.inventory.getStackInSlot(i).getItem() != null && player.inventory.getStackInSlot(i).getItem() != null && player.inventory.getStackInSlot(i).getItem() != null) {
                return true;
            }
        }
        return false;
    }
}
