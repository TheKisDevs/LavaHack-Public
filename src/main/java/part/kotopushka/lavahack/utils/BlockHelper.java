package part.kotopushka.lavahack.utils;


import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3f;

import com.kisman.cc.module.render.Breadcrumbs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;


public class BlockHelper
        implements Helper {

   public  static Minecraft mc = Minecraft.getMinecraft();
    public static Block getBlock(int x, int y, int z) {
        return BlockHelper.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static Block getBlock(BlockPos pos) {
        return BlockHelper.getState(pos).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return BlockHelper.mc.world.getBlockState(pos);
    }

    public static BlockPos getPlayerPosLocal() {
        if (BlockHelper.mc.player == null) {
            return BlockPos.ORIGIN;
        }
        return new BlockPos(Math.floor(BlockHelper.mc.player.posX), Math.floor(BlockHelper.mc.player.posY), Math.floor(BlockHelper.mc.player.posZ));
    }

    public static boolean insideBlock() {
        for (int x = MathHelper.floor(Helper.mc.player.boundingBox.minX); x < MathHelper.floor(Helper.mc.player.boundingBox.maxX) + 1; ++x) {
            for (int y = MathHelper.floor(Helper.mc.player.boundingBox.minY); y < MathHelper.floor(Helper.mc.player.boundingBox.maxY) + 1; ++y) {
                for (int z = MathHelper.floor(Helper.mc.player.boundingBox.minZ); z < MathHelper.floor(Helper.mc.player.boundingBox.maxZ) + 1; ++z) {
                    Block block = Helper.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block == null || block instanceof BlockAir) continue;
                    AxisAlignedBB boundingBox = block.getCollisionBoundingBox(Helper.mc.world.getBlockState(new BlockPos(x, y, z)), Helper.mc.world, new BlockPos(x, y, z));
                    if (block instanceof BlockHopper) {
                        boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                    }
                    if (boundingBox == null || !Helper.mc.player.boundingBox.intersects(boundingBox)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean blockIsSlipperiness() {
        return BlockHelper.mc.world.getBlockState((BlockPos)new BlockPos((double)BlockHelper.mc.player.posX, (double)(BlockHelper.mc.player.posY - 1.0), (double)BlockHelper.mc.player.posZ)).getBlock().slipperiness == 0.98f;
    }

    public static boolean rayTrace(BlockPos pos, float yaw, float pitch) {
        Vec3d vec3d = BlockHelper.mc.player.getPositionEyes(1.0f);
        RayTraceResult result = BlockHelper.mc.world.rayTraceBlocks(vec3d,vec3d , false);
        return result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && pos.equals(result.getBlockPos());
    }

    public static boolean isAboveLiquid(Entity entity, double posY) {
        if (entity == null) {
            return false;
        }
        double n = entity.posY + posY;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (!(BlockHelper.mc.world.getBlockState(new BlockPos(i, (int)n, j)).getBlock() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean collideBlock(AxisAlignedBB axisAlignedBB, float boxYSize, ICollide collide) {
        for (int x = MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().minX); x < MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().minZ); z < MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().maxZ) + 1; ++z) {
                Block block = BlockHelper.getBlock(new BlockPos((double)x, axisAlignedBB.minY + (double)boxYSize, (double)z));
                if (collide.collideBlock(block)) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean collideBlockIntersects(AxisAlignedBB axisAlignedBB, ICollide collide) {
        for (int x = MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().minX); x < MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int z = MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().minZ); z < MathHelper.floor(BlockHelper.mc.player.getEntityBoundingBox().maxZ) + 1; ++z) {
                AxisAlignedBB boundingBox;
                BlockPos blockPos = new BlockPos((double)x, axisAlignedBB.minY, (double)z);
                Block block = BlockHelper.getBlock(blockPos);
                if (block == null || !collide.collideBlock(block) || (boundingBox = block.getCollisionBoundingBox(BlockHelper.getState(blockPos), BlockHelper.mc.world, blockPos)) == null || !BlockHelper.mc.player.getEntityBoundingBox().intersects(boundingBox)) continue;
                return true;
            }
        }
        return false;
    }

    public static Vector3f getBlock(float radius, int block) {
        Vector3f vector3f = null;
        float dist = radius;
        for (float i = radius; i >= -radius; i -= 1.0f) {
            for (float j = -radius; j <= radius; j += 1.0f) {
                for (float k = radius; k >= -radius; k -= 1.0f) {
                    int posX = (int)(BlockHelper.mc.player.posX + (double)i);
                    int posY = (int)(BlockHelper.mc.player.posY + (double)j);
                    int posZ = (int)(BlockHelper.mc.player.posZ + (double)k);
                    float curDist = (float)BlockHelper.mc.player.getDistance(posX, posY, posZ);
                    if (Block.getIdFromBlock(BlockHelper.getBlock(posX, posY - 1, posZ)) != block || !(BlockHelper.getBlock(posX, posY, posZ) instanceof BlockAir) || !(curDist <= dist)) continue;
                    dist = curDist;
                    vector3f = new Vector3f(posX, posY, posZ);
                }
            }
        }
        return vector3f;
    }

    public static boolean IsValidBlockPos(BlockPos pos) {
        IBlockState state = BlockHelper.mc.world.getBlockState(pos);
        if (state.getBlock() instanceof BlockDirt || state.getBlock() instanceof BlockGrass && !(state.getBlock() instanceof BlockFarmland)) {
            return BlockHelper.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR;
        }
        return false;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circulate = new ArrayList<BlockPos>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                int y = sphere ? cy - (int)r : cy;
                while (true) {
                    float f;
                    float f2 = f = sphere ? (float)cy + r : (float)(cy + h);
                    if (!((float)y < f)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double)(r * r)) || hollow && dist < (double)((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circulate.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circulate;
    }

    public static ArrayList<BlockPos> getBlocks(int x, int y, int z) {
        BlockPos min = new BlockPos(BlockHelper.mc.player.posX - (double)x, BlockHelper.mc.player.posY - (double)y, BlockHelper.mc.player.posZ - (double)z);
        BlockPos max = new BlockPos(BlockHelper.mc.player.posX + (double)x, BlockHelper.mc.player.posY + (double)y, BlockHelper.mc.player.posZ + (double)z);
        return BlockHelper.getAllInBox(min, max);
    }

    public static ArrayList<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int y = min.getY(); y <= max.getY(); ++y) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static interface ICollide {
        public boolean collideBlock(Block var1);
    }
}


