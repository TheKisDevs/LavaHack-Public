package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.chat.*;
import com.kisman.cc.module.render.NoRender;
import com.kisman.cc.util.MathUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiNewChat.class, priority = 10000)
public class MixinGuiNewChat {
    @Shadow public boolean isScrolled;
    private float percentComplete;
    private int newLines;
    private long prevMillis;
    private float animationPercent;
    private int lineBeingDrawn;

    public MixinGuiNewChat() {
        this.prevMillis = System.currentTimeMillis();
    }

    @Shadow
    public float getChatScale() {return 0;}

    private void updatePercentage(final long diff) {
        if (percentComplete < 1.0f) percentComplete += 0.004f * diff;
        percentComplete = (float) MathUtil.clamp(percentComplete, 0.0, 1.0);
    }

    @Inject(method = "drawChat", at = @At("HEAD"))
    private void modifyChatRendering(CallbackInfo ci) {
        ChatModifier chatModifier = (ChatModifier) Kisman.instance.moduleManager.getModule("ChatModifier");
        if(chatModifier.isToggled() && chatModifier.getAnimation().getValBoolean()) {
            final long current = System.currentTimeMillis();
            final long diff = current - this.prevMillis;
            this.prevMillis = current;
            this.updatePercentage(diff);
            float t = this.percentComplete;
            this.animationPercent = (float) MathUtil.clamp(1.0f - --t * t * t * t, 0.0, 1.0);
        }
    }

    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    private void translate(CallbackInfo ci) {
        ChatModifier chatModifier = (ChatModifier) Kisman.instance.moduleManager.getModule("ChatModifier");
        if (chatModifier.isToggled() && chatModifier.getAnimation().getValBoolean()) {
            float y = 1.0f;
            if (!this.isScrolled) y += (9.0f - 9.0f * this.animationPercent) * this.getChatScale();
            int customY = 0;
            if(chatModifier.getCustomY().getValBoolean()) customY = chatModifier.getCustomYVal().getValInt();
            GlStateManager.translate(0.0f, y -customY, 0.0f);
        }
    }

    @ModifyArg(method = "drawChat" , at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false), index = 0)
    private int getLineBeingDrawn(int line) {
        return this.lineBeingDrawn = line;
    }

    @Redirect(method = {"drawChat"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawBackground(int left, int top, int right, int bottom, int color) {
        if(NoRender.instance.chatBackground.getValBoolean()) return;

        Gui.drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = {"drawChat"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color) {
        int newY = (int) y;
        if (this.lineBeingDrawn <= this.newLines) {
            int opacity = (int) y >> 24 & 0xFF;
            opacity *= (int)this.animationPercent;
            newY = ((int) y & 0xFFFFFF) | opacity << 24;
        }
        ChatModifier chatModifier = (ChatModifier) Kisman.instance.moduleManager.getModule("ChatModifier");
        if(chatModifier.isToggled() && chatModifier.getTtf().getValBoolean()) return CustomFontUtil.drawStringWithShadow(text, x, newY, color);
        return fontRenderer.drawStringWithShadow(text, x, newY, color);
    }

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private void resetPercentage(final CallbackInfo ci) {
        this.percentComplete = 0.0f;
    }

    @ModifyVariable(method = "setChatLine", at = @At("STORE"), ordinal = 0)
    private List<ITextComponent> setNewLines(final List<ITextComponent> original) {
        this.newLines = original.size() - 1;
        return original;
    }

    @ModifyVariable(method = "getChatComponent", at = @At(value = "STORE", ordinal = 0), ordinal = 4)
    private int modifyY(int original) {
        return original + 1;
    }
}
