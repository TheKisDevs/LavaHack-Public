package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.*;
import com.kisman.cc.settings.util.BoxRendererPattern;
import com.kisman.cc.util.ColourUtilKt;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.*;

public class StorageESP extends Module{
    private final Setting distance = new Setting("Distance(Squared)", this, 4000, 10, 4000, true);
    private final Setting colorAlpha = new Setting("Color Alpha", this, 255, 0, 255, true);

    boolean chest = true;
    boolean eChest = true;
    boolean shulkerBox = true;
    boolean dispenser = true;
    boolean furnace = true;
    boolean hopper = true;
    boolean dropper = true;

    private final BoxRendererPattern renderer = new BoxRendererPattern(this);

    public StorageESP() {
        super("StorageESP", "sosat", Category.RENDER);

        setmgr.rSetting(distance);
        setmgr.rSetting(colorAlpha);

        Kisman.instance.settingsManager.rSetting(new Setting("Chest", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("EChest", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("ShulkerBox", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Dispenser", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Furnace", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Hopper", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Dropper", this, true));

        renderer.init();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        chest = Kisman.instance.settingsManager.getSettingByName(this, "Chest").getValBoolean();
        eChest = Kisman.instance.settingsManager.getSettingByName(this, "EChest").getValBoolean();
        shulkerBox = Kisman.instance.settingsManager.getSettingByName(this, "ShulkerBox").getValBoolean();
        dispenser = Kisman.instance.settingsManager.getSettingByName(this, "Dispenser").getValBoolean();
        furnace = Kisman.instance.settingsManager.getSettingByName(this, "Furnace").getValBoolean();
        hopper = Kisman.instance.settingsManager.getSettingByName(this, "Hopper").getValBoolean();
        dropper = Kisman.instance.settingsManager.getSettingByName(this, "Dropper").getValBoolean();

        mc.world.loadedTileEntityList.stream()
            .filter(tileEntity -> tileEntity.getDistanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) <= distance.getValDouble())
            .forEach(tileEntity -> {
                if(tileEntity instanceof TileEntityChest && chest) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getChestColor(), tileEntity.getPos(), colorAlpha.getValInt());
                if(tileEntity instanceof TileEntityEnderChest && eChest) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getEnderChestColor(), tileEntity.getPos(), colorAlpha.getValInt());
                if(tileEntity instanceof TileEntityShulkerBox && shulkerBox) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getShulkerBoxColor(), tileEntity.getPos(), colorAlpha.getValInt());
                if(tileEntity instanceof TileEntityDispenser && dispenser) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getDispenserColor(), tileEntity.getPos(), colorAlpha.getValInt());
                if(tileEntity instanceof TileEntityFurnace && furnace) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getFurnaceColor(), tileEntity.getPos(), colorAlpha.getValInt());
                if(tileEntity instanceof TileEntityHopper && hopper) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getHopperColor(), tileEntity.getPos(), colorAlpha.getValInt());
                if(tileEntity instanceof TileEntityDropper && dropper) renderer.draw(event.getPartialTicks(), ColourUtilKt.BlockColors.Companion.getDropperColor(), tileEntity.getPos(), colorAlpha.getValInt());

                /*if(tileEntity instanceof TileEntityChest && chest) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.94f, 0.60f, 0.11f);
                if(tileEntity instanceof TileEntityEnderChest && eChest) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.53f, 0.11f, 0.94f);
                if(tileEntity instanceof TileEntityShulkerBox && shulkerBox) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.8f, 0.08f, 0.93f);
                if(tileEntity instanceof TileEntityDispenser && dispenser) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                if(tileEntity instanceof TileEntityFurnace && furnace) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                if(tileEntity instanceof TileEntityHopper && hopper) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);
                if(tileEntity instanceof TileEntityDropper && dropper) RenderUtil.drawBlockESP(tileEntity.getPos(), 0.34f, 0.32f, 0.34f);*/
            }
        );
    }
}
