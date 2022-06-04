package com.kisman.cc.util.chat.cubic;

import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class AbstractChatMessage {

    final Minecraft mc;

    Class<?> curCaller;

    AbstractChatMessage(){
        this.mc = Minecraft.getMinecraft();
        curCaller = null;
    }

    void updateCaller(Class<?> caller){
        this.curCaller = caller;
    }

    final String formatModule(){
        Module m = ChatUtility.moduleMapping.get(curCaller);
        if(m == null)
            return "null";
        return m.getName();
    }

    final String callerName(){
        return this.curCaller.getSimpleName();
    }

    public final void printMessage(ITextComponent textComponent){
        if(mc.player == null)
            return;
        mc.ingameGUI.getChatGUI().printChatMessage(textComponent);
    }

    public final void printMessage(String textComponentMessage){
        printMessage(new TextComponentTranslation(textComponentMessage));
    }


    public abstract void printClientMessage(ITextComponent textComponent);

    public abstract void printClientMessage(String message);


    public abstract void printClientModuleMessage(ITextComponent textComponent);

    public abstract void printClientModuleMessage(String message);


    public abstract void printModuleMessage(ITextComponent textComponent);

    public abstract void printModuleMessage(String message);


    public abstract void printClassMessage(ITextComponent textComponent);

    public abstract void printClassMessage(String message);


    public abstract void printClientClassMessage(ITextComponent textComponent);

    public abstract void printClientClassMessage(String message);
}
