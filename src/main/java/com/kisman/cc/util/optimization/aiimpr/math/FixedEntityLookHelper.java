package com.kisman.cc.util.optimization.aiimpr.math;

import net.minecraft.entity.ai.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;

public class FixedEntityLookHelper extends EntityLookHelper {
    public FixedEntityLookHelper(final EntityLiving entity) {
        super(entity);
    }
    
    public void onUpdateLook() {
        entity.rotationPitch = 0.0f;
        if (isLooking) {
            isLooking = false;
            final double d0 = this.posX - this.entity.posX;
            final double d2 = this.posY - (this.entity.posY + this.entity.getEyeHeight());
            final double d3 = this.posZ - this.entity.posZ;
            final double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
            final float f = (float)(tan(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            final float f2 = (float)(-(tan(d2, d4) * 180.0 / 3.141592653589793));
            entity.rotationPitch = updateRotation(entity.rotationPitch, f2, deltaLookPitch);
            entity.rotationYawHead = updateRotation(entity.rotationYawHead, f, deltaLookYaw);
        } else entity.rotationYawHead = updateRotation(entity.rotationYawHead, entity.renderYawOffset, 10.0f);
        final float f3 = MathHelper.wrapDegrees(entity.rotationYawHead - entity.renderYawOffset);
        if (!entity.getNavigator().noPath()) {
            if (f3 < -75.0f) entity.rotationYawHead = entity.renderYawOffset - 75.0f;
            if (f3 > 75.0f) entity.rotationYawHead = entity.renderYawOffset + 75.0f;
        }
    }
    
    public static float tan(final double a, final double b) {
        return FastTrig.atan2(a, b);
    }
}
