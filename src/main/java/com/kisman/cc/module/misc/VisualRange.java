package com.kisman.cc.module.misc;

import com.kisman.cc.module.*;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class  VisualRange extends Module {
    private final ArrayList<String> names;
    private final ArrayList<String> newnames;

    public VisualRange() {
        super("VisualRange", "", Category.MISC);

        this.names = new ArrayList<>();
        this.newnames = new ArrayList<>();
    }

    public void update() {
        this.newnames.clear();
        try {
            for (final Entity entity : mc.world.loadedEntityList) if (entity instanceof EntityPlayer && !entity.getName().equalsIgnoreCase(mc.player.getName())) this.newnames.add(entity.getName());
            if (!this.names.equals(this.newnames)) {
                for (final String name : this.newnames) if (!this.names.contains(name)) ChatUtils.warning(name + " entered in visual range!");
                for (final String name : this.names) if (!this.newnames.contains(name)) ChatUtils.message(name + " left from visual range!");
                this.names.clear();
                this.names.addAll(this.newnames);
            }
        } catch (Exception ignored) {}
    }
}
