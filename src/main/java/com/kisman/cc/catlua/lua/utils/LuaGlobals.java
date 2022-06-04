package com.kisman.cc.catlua.lua.utils;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.ModuleManager;
import com.kisman.cc.util.CrystalUtils;
import com.kisman.cc.util.Globals;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuaGlobals implements Globals {

    private static LuaGlobals instance;

    LuaGlobals() {
    }

    public int getFps() {
        return (mc.fpsCounter);
    }

    public double getFrametime() {
        return 1.0 / mc.fpsCounter;
    }

    public Vec3d getPosition() {
        if(mc.player == null) return Vec3d.ZERO;
        return mc.player.getPositionVector();
    }

    public long currentTime() {
        return System.currentTimeMillis();
    }

    public String getServer() {
        if(mc.player == null || mc.getCurrentServerData() == null) return "none";
        return mc.getCurrentServerData().serverIP;
    }

    public String getUsername() {
        return mc.getSession().getUsername();
    }

    public int getPing() {
        return nullCheck() ? 0 : (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing());
    }

    public void setTickMultiplier(float tick) {

    }

    public float getTickMultiplier() {
        return 20;
    }

    public Block getBlock(double x, double y, double z) {
        if(mc.world == null) return Blocks.AIR;
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public List<Entity> getEntities() {
        if (mc.world == null) return Collections.emptyList();
        return mc.world.loadedEntityList;
    }

    public List<EntityPlayer> getPlayerEntities() {
        if (mc.world == null) return Collections.emptyList();
        return mc.world.playerEntities;
    }

    public List<TileEntity> getTileEntities() {
        if (mc.world == null) return Collections.emptyList();
        return mc.world.loadedTileEntityList;
    }

    public int[] intArray(LuaTable table) {
        int[] ints = new int[table.length()];
        for (int j = 1; j <= table.length(); j++) {
            ints[j - 1] = table.get(j).toint();
        }
        return ints;
    }

    public float[] floatArray(LuaTable table) {
        float[] ints = new float[table.length()];
        for (int j = 1; j <= table.length(); j++) {
            ints[j - 1] = table.get(j).tofloat();
        }
        return ints;
    }

    public double[] doubleArray(LuaTable table) {
        double[] ints = new double[table.length()];
        for (int j = 1; j <= table.length(); j++) {
            ints[j - 1] = table.get(j).todouble();
        }
        return ints;
    }

    public char[] charArray(LuaTable table) {
        char[] ints = new char[table.length()];
        for (int j = 1; j <= table.length(); j++) {
            ints[j - 1] = table.get(j).tochar();
        }
        return ints;
    }

    public <T> T[] customArray(LuaTable table, Class<T> type) {
        Object[] ints = new Object[table.length()];
        for (int j = 1; j <= table.length(); j++) {
            ints[j - 1] = CoerceLuaToJava.coerce(table.get(j), type);
        }
        return ( T[] ) ints;
    }

    public static List<BlockPos> getSphere(float range, boolean sphere, boolean hollow) {
        return CrystalUtils.getSphere(range, sphere, hollow);
    }

    public static ScaledResolution getScaledResolution() {
        return new ScaledResolution(mc);
    }

    public static ModuleManager getModuleManager() {
        return Kisman.instance.moduleManager;
    }

    public static LuaGlobals getDefault() {
        if(instance == null) instance = new LuaGlobals();
        return instance;
    }

    public String getKeyName(int key) {return Keyboard.getKeyName(key);}

    public ArrayList<Category> getCategories() {
        return new ArrayList<>(Arrays.asList(Category.values()));
    }

    public boolean hover(LuaVec2d point, LuaVec2d from, LuaVec2d to) {
        return point.x > from.x && point.x < to.x && point.y > from.y && point.y < to.y;
    }

    public int getMouseWheel() {
        return Mouse.getDWheel();
    }

    public boolean instanceOf(Object o, String clazz) {
        Class<?> clazz1 = o.getClass();
        //TODO: remap
        while(clazz1 != null) {
            if(clazz1.getName().equals(clazz)) return true;
            clazz1 = clazz1.getSuperclass();
        }
        return false;
    }

    public void openGui(GuiScreen gui) {
        mc.displayGuiScreen(gui);
    }

    public void sendLocalMessage(String message) {
        ChatUtils.message(message);
    }

    public void sendGlobalMessage(String message) {
        mc.player.sendChatMessage(message);
    }
}
