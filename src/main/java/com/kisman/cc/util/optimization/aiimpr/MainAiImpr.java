package com.kisman.cc.util.optimization.aiimpr;

import com.kisman.cc.util.optimization.aiimpr.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.Iterator;

public class MainAiImpr {
    public static boolean ENABLED = false;
    public static boolean REMOVE_LOOK_AI = false;
    public static boolean REMOVE_LOOK_IDLE = false;
    public static boolean REPLACE_LOOK_HELPER = true;

    public void init() {
        FastTrig.init();
    }

    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(!ENABLED) return;
        final Entity entity = event.getEntity();
        if (entity instanceof EntityLiving) {
            final EntityLiving living = (EntityLiving)entity;
            if (REMOVE_LOOK_AI || REMOVE_LOOK_IDLE) {
                final Iterator it = living.tasks.taskEntries.iterator();
                while (it.hasNext()) {
                    final Object obj = it.next();
                    if (obj instanceof EntityAITasks.EntityAITaskEntry) {
                        final EntityAITasks.EntityAITaskEntry task = (EntityAITasks.EntityAITaskEntry)obj;
                        if(!(REMOVE_LOOK_AI && task.action instanceof EntityAIWatchClosest)) if (!REMOVE_LOOK_IDLE || !(task.action instanceof EntityAILookIdle)) continue;
                        it.remove();
                    }
                }
            }
            if (REPLACE_LOOK_HELPER && (living.getLookHelper() == null || living.getLookHelper().getClass() == EntityLookHelper.class)) {
                final EntityLookHelper oldHelper = living.lookHelper;
                living.lookHelper = new FixedEntityLookHelper(living);
                living.lookHelper.posX = oldHelper.posX;
                living.lookHelper.posY = oldHelper.posY;
                living.lookHelper.posZ = oldHelper.posZ;
                living.lookHelper.isLooking = oldHelper.isLooking;
                living.lookHelper.deltaLookPitch = oldHelper.deltaLookPitch;
                living.lookHelper.deltaLookYaw = oldHelper.deltaLookYaw;
            }
        }
    }
}
