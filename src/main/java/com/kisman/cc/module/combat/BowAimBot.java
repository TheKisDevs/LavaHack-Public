package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.Vec3d;

public class BowAimBot extends Module {
    private Setting maxDist = new Setting("Max Distance", this, 20, 1, 50, true);

    public BowAimBot() {
        super("BowAimBot", "", Category.COMBAT);

        setmgr.rSetting(maxDist);
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            EntityPlayer player = null;
            float tickDis = 100f;
            for (EntityPlayer p : mc.world.playerEntities) {
                if (p == mc.player) continue;
                if(p.getDistance(mc.player) > maxDist.getValInt()) continue;
                float dis = p.getDistance(mc.player);
                if (dis < tickDis) {
                    tickDis = dis;
                    player = p;
                }
            }
            if (player != null) {
                Vec3d pos = EntityUtil.getInterpolatedPos(player, mc.getRenderPartialTicks());
                float[] angels = AngleUtil.calculateAngle(EntityUtil.getInterpolatedPos(mc.player, mc.getRenderPartialTicks()), pos);
                mc.player.rotationYaw = angels[0];
                mc.player.rotationPitch = angels[1];
            }
        }
    }
}
