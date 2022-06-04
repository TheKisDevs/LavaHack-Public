package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.client.Cape;
import com.mojang.authlib.GameProfile;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractClientPlayer.class, priority = 10000)
public class MixinAbstractClientPlayer extends EntityPlayer {
    @Shadow public NetworkPlayerInfo playerInfo;
    private int count = 0;
    private final TimerUtils timer = new TimerUtils();

    public MixinAbstractClientPlayer(World worldIn, GameProfile gameProfileIn) {super(worldIn, gameProfileIn);}

    @Shadow public boolean isSpectator() {return true;}
    @Override public boolean isCreative() {return false;}

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    private void getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if(Cape.instance.isToggled() && playerInfo == Minecraft.getMinecraft().player.getPlayerInfo() || Kisman.instance.capeAPI.is(playerInfo.getGameProfile().getId())) {
            switch(Cape.instance.mode.getValString()) {
                case "Gif":
                    cir.setReturnValue(getCape());
                    break;
                case "Xulu+":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/xuluplus/xulupluscape.png"));
                    break;
                case "Kuro":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/kuro/kuro.png"));
                    break;
                case "GentleManMC":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/gentlemanmc/GentlemanMC.png"));
                    break;
                case "Putin":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/putin/putin.png"));
                    break;
                case "Gradient":
                    cir.setReturnValue(new ResourceLocation("kismancc:cape/gradient/gradient.png"));
                    break;
            }
        }
    }

    private ResourceLocation getCape() {
        if(count > 34) count = 0;

        final ResourceLocation cape = new ResourceLocation("kismancc:cape/rainbow/cape-" + count + ".png");

        if(timer.passedMillis(85)) {
            count++;
            timer.reset();
        }

        return cape;
    }
}