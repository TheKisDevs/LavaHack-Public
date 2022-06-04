package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.lua.EventRender2D;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import me.zero.alpine.listener.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class DamageESP extends Module {
    private final Setting timeToRemove = new Setting("Time To Remove", this, 3, 1, 5, true);
    private final Setting range = new Setting("Range", this, 20, 1, 50, true);
    private final Setting self = new Setting("Self", this, true);
    private final Setting heal = new Setting("Heal", this, true);
    private final Setting healColor = new Setting("Heal Color", this, "Heal Color", new Colour(0, 255, 0)).setVisible(heal::getValBoolean);
    private final Setting damage = new Setting("Damage", this, true);
    private final Setting damageColor = new Setting("Damage Color", this, "Damage Color", new Colour(255, 0, 0)).setVisible(damage::getValBoolean);

    private final HashMap<Entity, Float> entityHealthMap = new HashMap<>();
    private final List<Damage> damages = new ArrayList<>();

    public DamageESP() {
        super("DamageESP", "Thank you, gerald(man)", Category.RENDER);

        setmgr.rSetting(timeToRemove);
        setmgr.rSetting(range);
        setmgr.rSetting(self);
        setmgr.rSetting(heal);
        setmgr.rSetting(healColor);
        setmgr.rSetting(damage);
        setmgr.rSetting(damageColor);
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        entityHealthMap.clear();
        damages.clear();
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        for(Entity e : mc.world.loadedEntityList) {
            if(e == mc.player && !self.getValBoolean()) continue;
            if(e instanceof EntityLiving) {
                EntityLiving entity = (EntityLiving) e;
                if(!entityHealthMap.containsKey(entity)) entityHealthMap.put(entity, entity.getHealth());
                else {
                    if(entityHealthMap.get(entity) > entity.getHealth()) {
                        damages.add(new Damage(e, System.currentTimeMillis(), (entityHealthMap.get(entity) - entity.getHealth()), 1));
                        entityHealthMap.replace(entity, entity.getHealth());
                    } else if(entityHealthMap.get(entity) < entity.getHealth()) {
                        damages.add(new Damage(e, System.currentTimeMillis(), (entity.getHealth() - entityHealthMap.get(entity)), 2));
                        entityHealthMap.replace(entity, entity.getHealth());
                    }
                }
            }else if(e instanceof EntityPlayer) {
                EntityPlayer entity = (EntityPlayer) e;
                if(!entityHealthMap.containsKey(entity)) entityHealthMap.put(entity, entity.getHealth());
                else {
                    if(entityHealthMap.get(entity) > entity.getHealth()) {
                        damages.add(new Damage(e, System.currentTimeMillis(), (entityHealthMap.get(entity) - entity.getHealth()), 1));
                        entityHealthMap.replace(entity, entity.getHealth());
                    } else if(entityHealthMap.get(entity) < entity.getHealth()) {
                        damages.add(new Damage(e, System.currentTimeMillis(), (entity.getHealth() - entityHealthMap.get(entity)), 2));
                        entityHealthMap.replace(entity, entity.getHealth());
                    }
                }
            }
        }
    }

    @EventHandler
    private final Listener<EventRender2D> listener = new Listener<>(event -> {
        if(damages.isEmpty()) return;
        for(Damage damage : damages) {
            if(mc.player.getDistance(damage.entity) > range.getValInt()) continue;
            if(System.currentTimeMillis() - damage.startTime >= timeToRemove.getValInt() * 1000L) {
                damages.remove(damage);
                return;
            }
            if(mc.player.getDistance(damage.entity) > range.getValInt()) continue;
            final double x = damage.getEntity().getPosition().getX() + (damage.getEntity().getPosition().getX() - damage.getEntity().getPosition().getX()) * event.particalTicks - mc.getRenderManager().viewerPosX;
            final double y = damage.getEntity().getPosition().getY() + (damage.getEntity().getPosition().getY() - damage.getEntity().getPosition().getY()) * event.particalTicks - mc.getRenderManager().viewerPosY + damage.getEntity().getEyeHeight() + 0.5;
            final double z = damage.getEntity().getPosition().getZ() + (damage.getEntity().getPosition().getZ() - damage.getEntity().getPosition().getZ()) * event.particalTicks - mc.getRenderManager().viewerPosZ;
            final float var10001 = (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            mc.entityRenderer.setupCameraTransform(event.particalTicks, 0);
            GL11.glTranslated(x, y, z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(mc.getRenderManager().playerViewX, var10001, 0.0f, 0.0f);
            GL11.glScaled(-0.041666668839752674, -0.041666668839752674, 0.041666668839752674);
            final long timeLeft = damage.startTime + 1000L - System.currentTimeMillis();
            float yPercentage;
            float sizePercentage;
            if (timeLeft < 75L) {
                sizePercentage = Math.min(timeLeft / 75.0f, 1.0f);
                yPercentage = Math.min(timeLeft / 75.0f, 1.0f);
            } else {
                sizePercentage = Math.min((System.currentTimeMillis() - damage.startTime) / 300.0f, 1.0f);
                yPercentage = Math.min((System.currentTimeMillis() - damage.startTime) / 600.0f, 1.0f);
            }
            Gui.drawRect(-100, -100, 100, 100, new Color(255, 0, 0, 0).getRGB());
            switch (damage.getStage()) {
                case 2:
                    if(!heal.getValBoolean()) return;
                    mc.fontRenderer.drawStringWithShadow(new DecimalFormat("#.#").format(damage.damage), 0, (int)(-yPercentage), healColor.getColour().getRGB());
                    break;
                case 1:
                    if(!this.damage.getValBoolean()) return;
                    mc.fontRenderer.drawStringWithShadow(new DecimalFormat("#.#").format(damage.damage), 0, (int)(-yPercentage), damageColor.getColour().getRGB());
                    break;
            }
            GL11.glDisable(3042);
            GL11.glEnable(3553);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
            GL11.glEnable(2929);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    });

    public static class Damage {
        public Damage(Entity entity, long startTime, float damage, int stage) {
            this.entity = entity;
            this.startTime = startTime;
            this.damage = damage;
            this.stage = stage;
        }

        private final Entity entity;
        private final long startTime;
        private final float damage;
        private final int stage;

        public Entity getEntity() {return entity;}
        public long getStartTime() {return startTime;}
        public float getDamage() {return damage;}
        public int getStage() {return stage;}
    }
}
