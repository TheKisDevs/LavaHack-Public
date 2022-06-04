package com.kisman.cc.module.chat

import com.kisman.cc.Kisman
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.random.Random

class ChatModifier : Module(
        "ChatModifier",
        "ChatAnimation + CustomY + Suffix + AntiSpamBypass + TTF + AutoGlobal",
        Category.CHAT
) {
    val animation = Setting("Animation", this, false)
    val suffix = Setting("Suffix", this, false)
    val antiSpamBypass = Setting("Anti Spam Bypass", this, false)
    val autoGlobal = Setting("Auto Global", this, false)
    val customY = Setting("Custom Y", this, false)
    val customYVal = Setting("Custom Y Value", this, 50.0, 0.0, 100.0, true).setVisible { customY.valBoolean }
    val ttf = Setting("TTF", this, false)

    init {
        setmgr.rSetting(animation)
        setmgr.rSetting(suffix)
        setmgr.rSetting(antiSpamBypass)
        setmgr.rSetting(autoGlobal)
        setmgr.rSetting(customY)
        setmgr.rSetting(customYVal)
        setmgr.rSetting(ttf)
    }

    @SubscribeEvent
    fun onChat(event: ClientChatEvent) {
        if (!event.message.startsWith("/") &&
                !event.message.startsWith(Kisman.instance.commandManager.cmdPrefixStr) &&
                !event.message.startsWith(".") &&
                !event.message.startsWith(",") &&
                !event.message.startsWith(";") &&
                !event.message.startsWith(":") &&
                !event.message.startsWith("-") &&
                !event.message.startsWith("+")) {
            if(autoGlobal.valBoolean) {
                event.message = "!${event.message}"
            }
            if(suffix.valBoolean) {
                event.message = "${event.message} | ${Kisman.getName()} own you and all"
            }
            if(antiSpamBypass.valBoolean) {
                event.message = "${event.message} | ${Random.nextInt()}"
            }
        }
    }
}