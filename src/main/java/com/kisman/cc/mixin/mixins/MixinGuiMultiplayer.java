package com.kisman.cc.mixin.mixins;

import com.kisman.cc.gui.alts.AltManagerGUI;
import com.kisman.cc.viaforge.ViaForge;
import com.kisman.cc.viaforge.gui.GuiProtocolSelector;
import com.kisman.cc.viaforge.protocol.ProtocolCollection;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(value = GuiMultiplayer.class, priority = 10000)
public class MixinGuiMultiplayer extends GuiScreen {
    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGui(CallbackInfo ci) {
        buttonList.add(new GuiButton(417, 7, 7, 60, 20, "Alts"));
        buttonList.add(new GuiButton(1337, 7, 7 * 2 + 20, 98, 20, ProtocolCollection.getProtocolById(ViaForge.getInstance().getVersion()).getName()));
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if(button.id == 417) mc.displayGuiScreen(new AltManagerGUI(this));
        else if (button.id == 1337) mc.displayGuiScreen(new GuiProtocolSelector(this));
    }
}
