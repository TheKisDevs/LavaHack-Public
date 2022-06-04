package com.kisman.cc.util.chat.cubic;

import com.kisman.cc.Kisman;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

class ChatError extends AbstractChatMessage {

    private static final String CLIENT_PREFIX = ChatFormatting.GRAY + "[" + ChatFormatting.RED + Kisman.NAME + ChatFormatting.GRAY + "] " + ChatFormatting.RESET;

    private TextComponentTranslation fModule(){
        return new TextComponentTranslation(ChatFormatting.GRAY + "[" + ChatFormatting.RED + formatModule() + ChatFormatting.GRAY + "] " + ChatFormatting.RESET);
    }

    private TextComponentTranslation fClass(){
        return new TextComponentTranslation(ChatFormatting.GRAY + "[" + ChatFormatting.RED + callerName() + ChatFormatting.GRAY + "] " + ChatFormatting.RESET);
    }

    @Override
    public void printClientMessage(ITextComponent textComponent) {
        printMessage(new TextComponentTranslation(CLIENT_PREFIX).appendSibling(textComponent));
    }

    @Override
    public void printClientMessage(String message) {
        printClientMessage(new TextComponentTranslation(message));
    }

    @Override
    public void printClientModuleMessage(ITextComponent textComponent) {
        printMessage(new TextComponentTranslation(CLIENT_PREFIX).appendSibling(fModule()).appendSibling(textComponent));
    }

    @Override
    public void printClientModuleMessage(String message) {
        printClientModuleMessage(new TextComponentTranslation(message));
    }

    @Override
    public void printModuleMessage(ITextComponent textComponent) {
        printMessage(fModule().appendSibling(textComponent));
    }

    @Override
    public void printModuleMessage(String message) {
        printModuleMessage(new TextComponentTranslation(message));
    }

    @Override
    public void printClassMessage(ITextComponent textComponent) {
        printMessage(fClass().appendSibling(textComponent));
    }

    @Override
    public void printClassMessage(String message) {
        printClassMessage(new TextComponentTranslation(message));
    }

    @Override
    public void printClientClassMessage(ITextComponent textComponent) {
        printMessage(new TextComponentTranslation(CLIENT_PREFIX).appendSibling(fClass()).appendSibling(textComponent));
    }

    @Override
    public void printClientClassMessage(String message) {
        printClientClassMessage(new TextComponentTranslation(message));
    }
}
