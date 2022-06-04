package com.kisman.cc.module.render;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.Rendering;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Cubic
 *
 * Work in progress, please don't yell at me if it doesn't work
 * Do not add to module manager when pushing a release
 */
public class SelfCityESP extends Module {

    private final Setting smart = register(new Setting("Smart", this, false));
    private final Setting enemyCheck = register(new Setting("EnemyCheck", this, true));
    private final Setting enemyRange = register(new Setting("EnemyRange", this, 1.0, 6.0, 15.0, false).setVisible(enemyCheck::getValBoolean));
    private final Setting ignoreFriends = register(new Setting("IgnoreFriends", this, true).setVisible(enemyCheck::getValBoolean));
    private final Setting surroundCheck = register(new Setting("SurroundCheck", this, false));
    private final Setting terrain = register(new Setting("Terrain", this, false));
    private final Setting color = register(new Setting("Color", this, "Color", new Colour(255, 255, 255)));
    private final Setting color1 = register(new Setting("Color1", this, "Color1", new Colour(255, 255, 255)));
    private final Setting renderMode = register(new Setting("RenderMode", this, "Both", Arrays.asList("Box", "Outline", "Both", "Gradient", "Glow")));
    private final Setting lineWidth = register(new Setting("LineWidth", this, 2.0, 1.0, 5.0, false));

    public SelfCityESP(){
        super("SelfCityESP", Category.RENDER);
    }

    private final List<BlockPos> positions = new ArrayList<>();

    @Override
    public void update(){
        // super.nullCheck() when?
        if(mc.world == null || mc.player == null)
            return;

        positions.clear();

        if(enemyCheck.getValBoolean() && !searchTarget())
            return;

        if(surroundCheck.getValBoolean() && !isSurrounded())
           return;

        if(smart.getValBoolean()){
            checkSmart();
            return;
        }

        checkNormal();
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        if(mc.world == null || mc.player == null)
            return;

        if(!isToggled())
            return;

        for(BlockPos pos : positions){
            AxisAlignedBB axisAlignedBB = Rendering.correct(new AxisAlignedBB(pos));
            switch (renderMode.getValString()){
                case "Box":
                    Rendering.draw(axisAlignedBB, lineWidth.getValFloat(), color.getColour(), color1.getColour(), Rendering.Mode.BOX);
                    break;
                case "Outline":
                    Rendering.draw(axisAlignedBB, lineWidth.getValFloat(), color.getColour(), color1.getColour(), Rendering.Mode.OUTLINE);
                    break;
                case "Both":
                    Rendering.draw(axisAlignedBB, lineWidth.getValFloat(), color.getColour(), color1.getColour(), Rendering.Mode.BOTH);
                    break;
                case "Gradient":
                    Rendering.draw(axisAlignedBB, lineWidth.getValFloat(), color.getColour(), color1.getColour(), Rendering.Mode.BOTH_GRADIENT);
                    break;
                case "Glow":
                    Rendering.draw(axisAlignedBB, lineWidth.getValFloat(), color.getColour(), color1.getColour().withAlpha(0), Rendering.Mode.GRADIENT_CUSTOM_OUTLINE);
                    break;
            }
        }
    }

    public boolean searchTarget(){
        double rangeSq = enemyRange.getValDouble() * enemyRange.getValDouble();
        for(EntityPlayer player : mc.world.playerEntities){
            if(!ignoreFriends.getValBoolean() && FriendManager.instance.isFriend(player.getName()))
                continue;
            if(player.getDistanceSq(mc.player) <= rangeSq)
                return true;
        }
        return false;
    }

    public void checkSmart(){

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        BlockPos pos;

        north : {
            pos = playerPos.north();
            // breaking bedrock ain't a thing lel
            if(blockState(pos).getBlock() == Blocks.BEDROCK)
                break north;
            // is the block is replaceable, we break
            if(blockState(pos).getBlock().isReplaceable(mc.world, pos))
                break north;
            boolean b1 = check(pos.west());
            boolean b2 = check(pos.north());
            boolean b3 = check(pos.east());
            boolean b4 = check(pos.west().north());
            boolean b5 = check(pos.east().north());
            if(b1 || b2 || b3 || b4 || b5)
                positions.add(pos);
        }

        east : {
            pos = playerPos.east();
            if(blockState(pos).getBlock() == Blocks.BEDROCK)
                break east;
            if(blockState(pos).getBlock().isReplaceable(mc.world, pos))
                break east;
            boolean b1 = check(pos.north());
            boolean b2 = check(pos.east());
            boolean b3 = check(pos.south());
            boolean b4 = check(pos.north().east());
            boolean b5 = check(pos.south().east());
            if(b1 || b2 || b3 || b4 || b5)
                positions.add(pos);
        }

        south : {
            pos = playerPos.south();
            if(blockState(pos).getBlock() == Blocks.BEDROCK)
                break south;
            if(blockState(pos).getBlock().isReplaceable(mc.world, pos))
                break south;
            boolean b1 = check(pos.east());
            boolean b2 = check(pos.south());
            boolean b3 = check(pos.west());
            boolean b4 = check(pos.east().south());
            boolean b5 = check(pos.west().south());
            if(b1 || b2 || b3 || b4 || b5)
                positions.add(pos);
        }

        west : {
            pos = playerPos.west();
            if(blockState(pos).getBlock() == Blocks.BEDROCK)
                break west;
            if(blockState(pos).getBlock().isReplaceable(mc.world, pos))
                break west;
            boolean b1 = check(pos.south());
            boolean b2 = check(pos.west());
            boolean b3 = check(pos.north());
            boolean b4 = check(pos.south().west());
            boolean b5 = check(pos.north().west());
            if(b1 || b2 || b3 || b4 || b5)
                positions.add(pos);
        }
    }

    /**
     * This method checks on 3 things
     * 1. Make sure pos.down() is obsidian or bedrock
     * 2. Make sure pos is an air block
     * 3. Make sure pos.up() is air too
     * if all of these things are true, a player could
     * place a crystal there.
     */
    public boolean check(BlockPos pos){
        IBlockState state1 = mc.world.getBlockState(pos);
        if(!state1.getBlock().isReplaceable(mc.world, pos))
            return false;
        IBlockState state2 = mc.world.getBlockState(pos.down());
        Block block = state2.getBlock();
        boolean b = block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
        IBlockState state3 = mc.world.getBlockState(pos.up());
        return state3.getBlock() == Blocks.AIR && b;
    }

    public boolean isSurrounded(){
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        BlockPos pos;

        // north
        pos = playerPos.north();
        if(surroundBlockCheck(pos))
            return false;

        // east
        pos = playerPos.east();
        if(surroundBlockCheck(pos))
            return false;

        // south
        pos = playerPos.south();
        if(surroundBlockCheck(pos))
            return false;

        // west
        pos = playerPos.west();
        if(surroundBlockCheck(pos))
            return false;

        return true;
    }

    public boolean surroundBlockCheck(BlockPos pos){
        if(terrain.getValBoolean()){
            return blockState(pos).getBlock().isReplaceable(mc.world, pos);
        }
        return blockState(pos).getBlock() != Blocks.OBSIDIAN || blockState(pos).getBlock() != Blocks.ENDER_CHEST || blockState(pos).getBlock() != Blocks.BEDROCK;
    }

    public IBlockState blockState(BlockPos pos){
        return mc.world.getBlockState(pos);
    }

    //public boolean isReplaceable(BlockPos pos){
    //    return blockState(pos).getBlock().isReplaceable(mc.world, pos);
    //}

    public void checkNormal(){
        /*
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        BlockPos pos;

        // north
        pos = playerPos.north();
        if(isPlayerNotInArea(pos))
            positions.add(pos);

        // east
        pos = playerPos.east();
        if(isPlayerNotInArea(pos))
            positions.add(pos);

        // south
        pos = playerPos.south();
        if(isPlayerNotInArea(pos))
            positions.add(pos);

        // west
        pos = playerPos.west();
        if(isPlayerNotInArea(pos))
            positions.add(pos);
         */
        double x = mc.player.posX;
        double z = mc.player.posZ;

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        BlockPos pos;

        double diff;

        north : {
            pos = playerPos.north();
            if(isReplaceable(pos))
                break north;
            diff = diff(pos.getZ(), z);
            if(diff > 1.3)
                positions.add(pos);
        }
        /*
        if(diff > 1.0){
            if(diff > 1.3)
                positions.add(pos);
        } else {
            if(diff > 0.3)
                positions.add(pos);
        }
         */

        east : {
            pos = playerPos.east();
            if(isReplaceable(pos))
                break east;
            diff = diff(pos.getX(), x);
            if(diff > 1.3)
                positions.add(pos);
        }

        south : {
            pos = playerPos.south();
            if(isReplaceable(pos))
                break south;
            diff = diff(pos.getZ(), z);
            if(diff > 1.3)
                positions.add(pos);
        }

        west : {
            pos = playerPos.west();
            if(isReplaceable(pos))
                break west;
            diff = diff(pos.getX(), x);
            if(diff > 1.3)
                positions.add(pos);
        }
    }

    public boolean isReplaceable(BlockPos pos){
        return blockState(pos).getBlock().isReplaceable(mc.world, pos);
    }

    public double diff(double a, double b){
        return Math.abs(a - b);
    }

    public boolean isPlayerNotInArea(BlockPos pos){
        return mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos)).isEmpty();
    }
}
