package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.TurnEvent;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.*;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeLook extends Module {
    private final Setting autoThirdPerson = new Setting("Auto Third Person", this, false);

    private float dYaw, dPitch;

    public FreeLook() {
        super("FreeLook", Category.MISC);

        setmgr.rSetting(autoThirdPerson);
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        dYaw = dPitch = 0;

        if(mc.player == null || mc.world == null) return;
        if (autoThirdPerson.getValBoolean()) mc.gameSettings.thirdPersonView = 1;
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        if(mc.player == null || mc.world == null) return;
        if (autoThirdPerson.getValBoolean()) mc.gameSettings.thirdPersonView = 0;
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (mc.gameSettings.thirdPersonView > 0) {
            event.setYaw(event.getYaw() + dYaw);
            event.setPitch(event.getPitch() + dPitch);
        }
    }

    @EventHandler
    private final Listener<TurnEvent> listener = new Listener<>(event -> {
        if (mc.gameSettings.thirdPersonView > 0) {
            dYaw = (float) ((double) dYaw + (double) event.yaw * 0.15D);
            dPitch = (float) ((double) dPitch - (double) event.pitch * 0.15D);
            dPitch = MathHelper.clamp(dPitch, -90.0F, 90.0F);
            event.cancel();
        }
    });
}
