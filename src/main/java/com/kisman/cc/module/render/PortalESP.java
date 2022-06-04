package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PortalESP extends Module {
    private final Setting range = new Setting("Range", this, 50, 0, 100, false);

    private final Setting nether = new Setting("Nether", this, true);
    private final Setting end = new Setting("End", this, true);

    public PortalESP() {
        super("PortalESP", "Shows nether/end portals", Category.RENDER);

        setmgr.rSetting(range);
        setmgr.rSetting(nether);
        setmgr.rSetting(end);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for(BlockPos pos : BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), (float) range.getValDouble(), range.getValInt(), false, true, 0)) if((mc.world.getBlockState(pos).getBlock().equals(Blocks.PORTAL) && nether.getValBoolean()) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.END_PORTAL) && end.getValBoolean())) RenderUtil.drawBlockESP(pos, 0.67f, 0, 1);
    }
}
