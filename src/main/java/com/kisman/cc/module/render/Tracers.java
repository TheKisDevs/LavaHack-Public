package com.kisman.cc.module.render;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.RenderUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tracers extends Module {
    private Setting players = new Setting("Players", this, false);
//    private Setting playersColor = new Setting("PlayersColor", this, "Color", new float[] {1, 1, 1, 1});
    private Setting playersAstolfo = new Setting("PlayersAstolfo", this, false);

    private Setting friends = new Setting("Friends", this, false);
//    private Setting friendsColor = new Setting("friendsColor", this, "Color", new float[] {1, 1, 1, 1});
    private Setting friendsAstolfo = new Setting("FriendsAstolfo", this, true);

    private Setting items = new Setting("Items", this, false);
//    private Setting itemsColor = new Setting("ItemsColor", this, "Color", new float[] {1, 1, 1, 1});
    private Setting itemsAstolfo = new Setting("ItemsAstolfo", this, true);

    public Tracers() {
        super("Tracers", "Tracers", Category.RENDER);

        setmgr.rSetting(players);
//        setmgr.rSetting(playersColor);
        setmgr.rSetting(playersAstolfo);
        setmgr.rSetting(friends);
//        setmgr.rSetting(friendsColor);
//        setmgr.rSetting(friendsAstolfo);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for(Entity entity : mc.world.loadedEntityList) {
            if(entity == mc.player) continue;

            if(entity instanceof EntityPlayer) {
                if(FriendManager.instance.isFriend((EntityPlayer) entity)) {
                    if(friends.getValBoolean()) {
//                        RenderUtil.drawTracer(entity, friendsAstolfo.getValBoolean() ? new Colour((float) friendsColor.getR() / 255f, (float) friendsColor.getG() / 255f, (float) friendsColor.getB() / 255f, (float) friendsColor.getA() / 255f) : new Colour(ColorUtils.astolfoColors(100, 100)), event.getPartialTicks());
                    }
                } else if(players.getValBoolean()) {
//                    RenderUtil.drawTracer(entity, playersAstolfo.getValBoolean() ? new Colour((float) playersColor.getR() / 255f, (float) playersColor.getG() / 255f, (float) playersColor.getB() / 255f, (float) friendsColor.getA() / 255f) : new Colour(ColorUtils.astolfoColors(100, 100)), event.getPartialTicks());
                }
            } else if(entity instanceof EntityItem && items.getValBoolean()) {
//                RenderUtil.drawTracer(entity, itemsAstolfo.getValBoolean() ? new Colour((float) itemsColor.getR() / 255f, (float) itemsColor.getG() / 255f, (float) itemsColor.getB() / 255f, (float) itemsColor.getA() / 255f) : new Colour(ColorUtils.astolfoColors(100, 100)), event.getPartialTicks());
            }
        }
    }
}