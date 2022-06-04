package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

public class BoatFly extends Module {
    private final Setting speed = new Setting("Speed", this, 2, 0, 10, true);
    private final Setting verticalSpeed = new Setting("Vertical Speed", this, 1, 0, 10, true);
    private final Setting downKey = new Setting("Down Key", this, Keyboard.KEY_LCONTROL).setVisible(verticalSpeed.getValInt() != 0);
    private final Setting glideSpeed = new Setting("Glide Speed", this, 0, -10, 10, true);
    private final Setting staticY = new Setting("Static Y", this, true);
    private final Setting hover = new Setting("Hover", this, false);
    private final Setting bypass = new Setting("Bypass", this, false);
    private final Setting extraCalc = new Setting("Extra Calc", this, false);

    public BoatFly() {
        super("BoatFly", Category.MOVEMENT);

        setmgr.rSetting(speed);
        setmgr.rSetting(verticalSpeed);
        setmgr.rSetting(downKey);
        setmgr.rSetting(glideSpeed);
        setmgr.rSetting(staticY);
        setmgr.rSetting(hover);
        setmgr.rSetting(bypass);
        setmgr.rSetting(extraCalc);
    }

    public void update() {
        if(mc.player == null || mc.world == null || mc.player.ridingEntity == null) return;
        super.setDisplayInfo("[" + speed.getValInt() + "]");
        Entity e = mc.player.ridingEntity;
        if (mc.gameSettings.keyBindJump.isKeyDown()) e.motionY = verticalSpeed.getValDouble();
        else if (!downKey.isNoneKey() && Keyboard.isKeyDown(downKey.getKey())) e.motionY = -verticalSpeed.getValDouble();
        else if(staticY.getValBoolean()) e.motionY = 0;
        else e.motionY = hover.getValBoolean() && mc.player.ticksExisted % 2 == 0 ? glideSpeed.getValDouble() : -glideSpeed.getValDouble();
        if (MovementUtil.isMoving()) {
            if(!extraCalc.getValBoolean()) {
                double[] dir = MovementUtil.forward(speed.getValDouble());
                e.motionX = dir[0];
                e.motionZ = dir[1];
            } else {
                float dir = MovementUtil.getDirection2();
                mc.player.motionX -= (MathHelper.sin(dir) * speed.getValFloat());
                mc.player.motionZ += (MathHelper.cos(dir) * speed.getValFloat());
            }
        } else {
            e.motionX = 0;
            e.motionZ = 0;
        }
        if (bypass.getValBoolean() && mc.player.ticksExisted % 4 == 0) if (mc.player.ridingEntity instanceof EntityBoat) mc.playerController.interactWithEntity(mc.player, mc.player.ridingEntity, EnumHand.MAIN_HAND);
    }
}
