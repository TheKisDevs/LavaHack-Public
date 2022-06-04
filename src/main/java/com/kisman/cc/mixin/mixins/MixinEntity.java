package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.TurnEvent;
import net.minecraft.entity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 10000)
public class MixinEntity {
    @Shadow public double posX;
    @Shadow public double posY;
    @Shadow public double posZ;
    @Shadow public double prevPosX;
    @Shadow public double prevPosY;
    @Shadow public double prevPosZ;
    @Shadow public double lastTickPosX;
    @Shadow public double lastTickPosY;
    @Shadow public double lastTickPosZ;
    @Shadow public float prevRotationYaw;
    @Shadow public float prevRotationPitch;
    @Shadow public float rotationPitch;
    @Shadow public float rotationYaw;
    @Shadow public boolean onGround;
    @Shadow public double motionX;
    @Shadow public double motionY;
    @Shadow public double motionZ;
    @Shadow public World world;
    @Shadow public void move(final MoverType type, final double x, final double y, final double z) {}
    @Shadow public AxisAlignedBB getEntityBoundingBox() {return null;}
    @Shadow protected boolean getFlag(final int p0) {return true;}
    @Shadow public Entity getLowestRidingEntity() {return null;}

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    public void onTurn(float yaw, float pitch, CallbackInfo ci) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        Kisman.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
