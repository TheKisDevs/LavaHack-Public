package com.kisman.cc.mixin;

import java.util.Map;

import javax.annotation.Nullable;

import com.kisman.cc.Kisman;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.*;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("ratka")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class KismanMixinLoader implements IFMLLoadingPlugin {

    public KismanMixinLoader(){
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.lavahack-public.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String[] getASMTransformerClass(){
        return new String[0];
    }

    @Override
    public String getModContainerClass(){
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass(){
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data){
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.kisman.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String getAccessTransformerClass(){
        return null;
    }
}
