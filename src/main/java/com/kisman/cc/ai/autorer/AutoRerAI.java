package com.kisman.cc.ai.autorer;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.combat.AutoRer;
import com.kisman.cc.util.*;
import me.zero.alpine.listener.*;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import org.neuroph.core.data.DataSet;

import java.util.List;

public class AutoRerAI {
    public static final AutoRerAI instance = new AutoRerAI();

    /*
    input: 6 (pos(x, y, z), distToTarget, targetDMG, selfDMG)
    output: 1 (true or false) or (0 or 1)
     */
    private static final DataSet dataSet = new DataSet(5, 1);
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static BlockPos placePos;

    static {
        Kisman.EVENT_BUS.subscribe(instance.listener);
    }

    public static void collect(BlockPos placePos, double targetDMG, double selfDMG) {
        calculatePlace();

        dataSet.addRow(
                new double[] {
                        placePos.getX(), placePos.getY(), placePos.getZ(),
                        targetDMG, selfDMG
                },
                new double[] {
                        getDMG(AutoRerAI.placePos, placePos)
                }
        );
        save();
    }

    private static double getDMG(BlockPos pos1, BlockPos pos2) {
        if(pos1 == null && pos2 == null) return 0.5;
        else if(pos1 == null) return CrystalUtils.calculateDamage(mc.world, pos2.getX() + 0.5, pos2.getY() + 1, pos2.getZ() + 0.5, AutoRer.currentTarget, AutoRer.instance.terrain.getValBoolean());
        else if(pos2 == null) return CrystalUtils.calculateDamage(mc.world, pos1.getX() + 0.5, pos1.getY() + 1, pos1.getZ() + 0.5, AutoRer.currentTarget, AutoRer.instance.terrain.getValBoolean());
        else {
            double dmg1 = CrystalUtils.calculateDamage(mc.world, pos1.getX() + 0.5, pos1.getY() + 1, pos1.getZ() + 0.5, AutoRer.currentTarget, AutoRer.instance.terrain.getValBoolean());
            double dmg2 = CrystalUtils.calculateDamage(mc.world, pos2.getX() + 0.5, pos2.getY() + 1, pos2.getZ() + 0.5, AutoRer.currentTarget, AutoRer.instance.terrain.getValBoolean());

            return dmg1 > dmg2 ? dmg1 : dmg1 == dmg2 ? dmg1 : dmg2;
        }
    }

    public static void save() {
        dataSet.save("dataSet.tset");
        dataSet.saveAsTxt("dataSet.txt", ";");
    }

    private static void calculatePlace() {
        double maxDamage = 0.5;
        BlockPos placePos = null;
        List<BlockPos> sphere = CrystalUtils.getSphere(AutoRer.instance.placeRange.getValFloat(), true, false);

        for(int size = sphere.size(), i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);

            if(CrystalUtils.canPlaceCrystal(pos, AutoRer.instance.secondCheck.getValBoolean())) {
                float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, AutoRer.currentTarget, AutoRer.instance.terrain.getValBoolean());

                if(targetDamage > AutoRer.instance.minDMG.getValInt() || targetDamage * AutoRer.instance.lethalMult.getValDouble() > AutoRer.currentTarget.getHealth() + AutoRer.currentTarget.getAbsorptionAmount() || InventoryUtil.isArmorUnderPercent(AutoRer.currentTarget, AutoRer.instance.armorBreaker.getValInt())) {
                    float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, AutoRer.instance.terrain.getValBoolean());

                    if(selfDamage <= AutoRer.instance.maxSelfDMG.getValInt() && selfDamage + 2 < mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage) {
                        if(maxDamage <= targetDamage) {
                            maxDamage = targetDamage;
                            placePos = pos;
                        }
                    }
                }
            }
        }

        AutoRerAI.placePos = placePos;
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) {
            BlockPos pos = ((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos();
            float targetDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, AutoRer.currentTarget, AutoRer.instance.terrain.getValBoolean());
            float selfDamage = CrystalUtils.calculateDamage(mc.world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player, AutoRer.instance.terrain.getValBoolean());

            collect(pos, targetDamage, selfDamage);
        }
    });
}
