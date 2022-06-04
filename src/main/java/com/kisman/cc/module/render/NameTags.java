package com.kisman.cc.module.render;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.customfont.norules.CFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

public class  NameTags extends Module {
    private Setting range = new Setting("Range", this, 0, 50 ,100, false);
    private Setting scale = new Setting("Scale", this, 0.1f, 0.1f, 0.3f, false);
    private Setting bgAlpha = new Setting("BG Alpha", this, 128, 0, 250, true);
    private Setting ping = new Setting("Ping", this, true);
    private Setting items = new Setting("Items", this, true);
    private Setting damageDisplay = new Setting("Damage Display", this, true);
    private Setting atheist = new Setting("Atheist", this, true);
    private Setting desc = new Setting("Desc", this, false);

    public static NameTags instance;

    private int counter1;
    private int counter2;
    private int color1;
    private HashMap<String, Integer> tagList = new HashMap<>();
    private HashMap<String, String> damageList = new HashMap<>();

    public NameTags() {
        super("NameTags", Category.RENDER);

        instance = this;

        setmgr.rSetting(range);
        setmgr.rSetting(scale);
        setmgr.rSetting(bgAlpha);
        setmgr.rSetting(ping);
        setmgr.rSetting(items);
        setmgr.rSetting(damageDisplay);
        setmgr.rSetting(atheist);
        setmgr.rSetting(desc);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for(EntityPlayer p : mc.world.playerEntities) {
            if (p != mc.getRenderViewEntity() && p.isEntityAlive()) {
                if (damageDisplay.getValBoolean()) {
                    if (!this.tagList.containsKey(p.getName())) {
                        this.tagList.put(p.getName(), (int)p.getHealth());
                        this.damageList.put(p.getName(), "");
                    }
                    if (p.isDead || p.getHealth() <= 0.0f) {
                        this.tagList.remove(p.getName());
                        this.damageList.remove(p.getName());
                    }
                }
                final double pX = p.lastTickPosX + (p.posX - p.lastTickPosX) * mc.timer.renderPartialTicks;
                final double pY = p.lastTickPosY + (p.posY - p.lastTickPosY) * mc.timer.renderPartialTicks;
                final double pZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * mc.timer.renderPartialTicks;
                Entity renderEntity = mc.getRenderManager().renderViewEntity;
                if (renderEntity == null) renderEntity = mc.player;
                if (renderEntity == null) return;
                final double rX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * mc.timer.renderPartialTicks;
                final double rY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * mc.timer.renderPartialTicks;
                final double rZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * mc.timer.renderPartialTicks;
                this.renderNametag(p, pX - rX, pY - rY, pZ - rZ);
            }
            if (this.counter2 == 601 && damageDisplay.getValBoolean()) {
                this.tagList.remove(p.getName());
                this.damageList.remove(p.getName());
            }
        }
        if (this.counter2 == 601) this.counter2 = 0;
        ++this.counter1;
        ++this.counter2;
    }

    public void renderNametag(final EntityPlayer player, final double x, final double y, final double z) {
        GL11.glPushMatrix();
        RenderUtil.enableDefaults();
        TextFormatting clr;
        TextFormatting clrf = TextFormatting.WHITE;
        String cross = "";
        if (FriendManager.instance.isFriend(player.getName())) {
            clrf = TextFormatting.AQUA;
            if (!atheist.getValBoolean()) cross = "\u271d ";
        }
        int pingy = -1;
        try {pingy = mc.player.connection.getPlayerInfo(player.getUniqueID()).getResponseTime();} catch (NullPointerException ignored) {}
        String playerPing = pingy + "ms  ";
        final boolean pings = this.ping.getValBoolean();
        if (!pings) playerPing = "";
        final int health = MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount());
        final boolean damageDisplay = this.damageDisplay.getValBoolean();
        if (health > 16) clr = TextFormatting.GREEN;
        else if (health > 12) clr = TextFormatting.YELLOW;
        else if (health > 8) clr = TextFormatting.GOLD;
        else if (health > 5) clr = TextFormatting.RED;
        else clr = TextFormatting.DARK_RED;
        final int lasthealth = this.tagList.get(player.getName());
        if (player != mc.player && damageDisplay) {
            if (lasthealth > health) this.damageList.put(player.getName(), TextFormatting.RED + " -" + (lasthealth - health));
            this.tagList.put(player.getName(), health);
        }
        String dmgtext = "";
        if (damageDisplay) dmgtext = this.damageList.get(player.getName());
        String name = cross + clrf + playerPing + player.getName() + " " + clr + health + dmgtext;
        name = name.replace(".0", "");
        final float var14 = 0.016666668f * this.getNametagSize(player);
        GL11.glTranslated(x, y + 2.5 + var14 * 10.0f, z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-var14, -var14, var14);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GL11.glDisable(2929);
        final CFontRenderer font = CustomFontUtil.sfui19;
        final int width = font.getStringWidth(name) / 2;
        final double widthBackGround = bgAlpha.getValDouble();
        final int[] counter = { 1 };
        this.color1 = twoColorEffect(new Color(255, 50, 50), new Color(79, 9, 9), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (counter[0] * 2.55) / 60.0).getRGB();
        RenderUtil.drawSmoothRect((float)(-width - 3), 9.0f, (float)(width + 4), 23.0f, new Color(0, 0, 0, (int)widthBackGround).getRGB());
        final int[] array = counter;
        final int n = 0;
        ++array[n];//9 + 14 / 2 - font.fontHeight / 2
        font.drawString(name, -width, 9 + 7 - (font.fontHeight - 8) / 4, Color.red.getRGB());
        boolean item = this.items.getValBoolean();
        if (item) {
            int xOffset = -8;
            for (final ItemStack armourStack : player.inventory.armorInventory) if (armourStack != null) xOffset -= 8;
            if (player.getHeldItemMainhand() != null) {
                xOffset -= 8;
                final ItemStack renderStack = player.getHeldItemMainhand().copy();
                this.renderItem(renderStack, xOffset, -10);
                xOffset += 16;
            }
            for (int index = 3; index >= 0; --index) {
                final ItemStack armourStack2 = player.inventory.armorInventory.get(index);
                if (armourStack2 != null) {
                    final ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(renderStack2, xOffset, -10);
                    xOffset += 16;
                }
            }
            if (player.getHeldItemOffhand() != null) {
                xOffset += 0;
                final Object renderOffhand = player.getHeldItemOffhand().copy();
                this.renderItem((ItemStack)renderOffhand, xOffset, -10);
                xOffset += 8;
            }
        }
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.disableDefaults();
    }

    public float getNametagSize(final EntityLivingBase player) {
        final ScaledResolution scaledRes = new ScaledResolution(mc);
        final double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0);
        final double scale = this.scale.getValDouble();
        return (float)scale * 6.0f * ((float)twoDscale + (float)(player.getDistance(mc.renderViewEntity.posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ) / 10.5));
    }

    public void renderItem(ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0f;
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y / 2 - 12);
        if (stack.getItem() == Items.GOLDEN_APPLE) RenderUtil.renderItemOverlays(CustomFontUtil.consolas16, stack, x - 5, y / 2 - 28);
        else RenderUtil.renderItemOverlays(CustomFontUtil.consolas16, stack, x, y / 2 - 8);
        mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        boolean enchants = desc.getValBoolean();
        if (enchants) renderEnchantText(stack, x, y - 18);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(ItemStack stack, int x, int y) {
        int encY = y - 18;
        int yCount = encY + 5;
        final NBTTagList enchants = stack.getEnchantmentTagList();
        if (enchants != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                final short id = enchants.getCompoundTagAt(index).getShort("id");
                final short level = enchants.getCompoundTagAt(index).getShort("lvl");
                final Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                    encName = encName + level;
                    GL11.glPushMatrix();
                    GL11.glScalef(1.0f, 1.0f, 0.0f);
                    CFontRenderer font = CustomFontUtil.futura20;
                    if (level == 1) font.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(202, 202, 202, 255).getRGB());
                    else if (level == 2) font.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(246, 218, 45, 255).getRGB());
                    else if (level == 3) font.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(229, 128, 0, 255).getRGB());
                    else if (level == 4) font.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(156, 59, 253, 255).getRGB());
                    else font.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(239, 0, 0, 255).getRGB());
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                    encY += 8;
                    yCount -= 10;
                }
            }
        }
    }

    public static Color twoColorEffect(Color color, Color color2, double delay) {
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int)delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int)(color.getRed() * n3 + color2.getRed() * delay), (int)(color.getGreen() * n3 + color2.getGreen() * delay), (int)(color.getBlue() * n3 + color2.getBlue() * delay), (int)(color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
}