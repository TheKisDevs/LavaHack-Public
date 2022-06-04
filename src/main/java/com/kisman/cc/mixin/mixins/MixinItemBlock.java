package com.kisman.cc.mixin.mixins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.kisman.cc.module.exploit.NoGlitchBlocks;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ItemBlock.class)
public class MixinItemBlock {
    @Shadow public boolean placeBlockAt(ItemStack stack,EntityPlayer player,World world,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,IBlockState state) {return false;}

        /**
     * {@link ItemBlock#placeBlockAt(ItemStack, EntityPlayer, World,
     * BlockPos, EnumFacing, float, float, float, IBlockState)}
     */
    @Dynamic
    @Redirect(
        method = "onItemUse",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/item/ItemBlock.placeBlockAt" +
                    "(Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/entity/player/EntityPlayer;" +
                    "Lnet/minecraft/world/World;" +
                    "Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/util/EnumFacing;" +
                    "FFF" +
                    "Lnet/minecraft/block/state/IBlockState;)Z",
            remap = false))
    private boolean onItemUseHook(ItemBlock block,
                                  ItemStack stack,
                                  EntityPlayer player,
                                  World world,
                                  BlockPos pos,
                                  EnumFacing facing,
                                  float hitX,
                                  float hitY,
                                  float hitZ,
                                  IBlockState state)
    {
        return world.isRemote
                && NoGlitchBlocks.instance.isToggled()
                && NoGlitchBlocks.instance.noPlace()
                    || this.placeBlockAt(stack,
                                         player,
                                         world,
                                         pos,
                                         facing,
                                         hitX,
                                         hitY,
                                         hitZ,
                                         state);
    }
}
