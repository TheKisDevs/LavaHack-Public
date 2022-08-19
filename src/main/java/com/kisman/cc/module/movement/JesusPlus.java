package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.NumberSetting;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MovementUtil;
import com.kisman.cc.util.PlayerUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;


public class JesusPlus extends Module {

     public int waterTicks = 0;
    public final Setting speedX = register(new Setting("Jesus Speed:", this, 1.0f, 0.01f, 5.0f,  false));
    private final Setting jesusmode = register(new Setting("Jesus Mode", this, "Matrix Zoom", Arrays.asList("Matrix Zoom", "SunRise", "Packet")));
    public JesusPlus(){
        super("JesusPlus", Category.MOVEMENT);
    }
    public void update(){
        if(mc.player == null || mc.world == null) return;
        if (Jesus.mc.world.getBlockState(new BlockPos(Jesus.mc.player.posX, Jesus.mc.player.posY - (double)0.2f, Jesus.mc.player.posZ)).getBlock() instanceof BlockLiquid && !Jesus.mc.player.onGround) {
            float Speedz = speedX.getValFloat();
            PlayerUtil.setSpeed(mc.player, Speedz);

        }
        if (!(Jesus.mc.world.getBlockState(new BlockPos(Jesus.mc.player.posX, Jesus.mc.player.posY + (double)1.0E-4f, Jesus.mc.player.posZ)).getBlock() instanceof BlockLiquid)) return;
        Jesus.mc.player.motionX = 0.0;
        Jesus.mc.player.motionZ = 0.0;
        Jesus.mc.player.motionY = 0.05f;


    }
    }
