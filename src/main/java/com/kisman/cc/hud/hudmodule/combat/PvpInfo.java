package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.movement.Speed;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.*;

public class PvpInfo extends HudModule {
    public PvpInfo() {
        super("PvpInfo", "PvpInfo", HudCategory.COMBAT, true);

        setX(1);
        setY(1);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRender(RenderGameOverlayEvent.Text event) {
        int y = (int) getY();
        int height = 2 + CustomFontUtil.getFontHeight();
        int count = 0;
        setW(CustomFontUtil.getStringWidth("Speed: OFF"));

        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "ReR: " + (isToggled(AutoRer.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "KA: " + (isToggled(KillAura.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "FA: " + (isToggled(AutoFirework.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "OFF: " + (isToggled(OffHand.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "SURR: " + (isToggled(Surround.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "AT: " + (isToggled(AutoTrap.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "HF: " + (isToggled(HoleFiller.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "CF: " + (isToggled(CrystalFiller.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "ATDB: " + (isToggled(AntiTrapDoorBurrow.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "Speed: " + (isToggled(Speed.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;

        setH(count * height);
    }

    private boolean isToggled(Module mod) {
        return mod.isToggled();
    }
}
