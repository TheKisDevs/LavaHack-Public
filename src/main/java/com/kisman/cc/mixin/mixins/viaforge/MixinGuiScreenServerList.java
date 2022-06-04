package com.kisman.cc.mixin.mixins.viaforge;

import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.viaforge.ViaForge;
import com.kisman.cc.viaforge.gui.GuiProtocolSelector;
import com.kisman.cc.viaforge.protocol.ProtocolCollection;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenServerList.class)
public class MixinGuiScreenServerList extends GuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    public void injectInitGui(CallbackInfo ci) {
        buttonList.add(new GuiButton(1337, 5, 6, 98, 20, ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName()));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void injectActionPerformed(GuiButton p_actionPerformed_1_, CallbackInfo ci) {
        if (p_actionPerformed_1_.id == 1337) mc.displayGuiScreen(new GuiProtocolSelector(this));
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void injectDrawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_, CallbackInfo ci) {
        CustomFontUtil.drawStringWithShadow("<-- Current Version", 104, 13, -1);
    }
}
