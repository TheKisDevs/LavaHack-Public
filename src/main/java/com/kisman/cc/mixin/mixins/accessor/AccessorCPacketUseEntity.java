package com.kisman.cc.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author moneymod
 */
@Mixin( value = { CPacketUseEntity.class } )
public interface AccessorCPacketUseEntity {
    @Accessor( value = "entityId" ) void setEntityId( int var1 );

    @Accessor( value = "action" ) void setAction( CPacketUseEntity.Action var1 );
}