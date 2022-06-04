package com.kisman.cc.util.process.web.discord

import com.kisman.cc.util.Colour
import java.io.IOException

class DiscordWebhookSender {
    companion object {
        fun send(webhook: String, color: Colour, title: String) {
            val sender = com.kisman.cc.util.discord.DiscordWebhook(webhook)
            sender.addEmbed(com.kisman.cc.util.discord.DiscordWebhook.EmbedObject().setColor(color.color).setTitle(title))

            try{
                sender.execute()
            } catch (e: IOException) {}
        }
    }
}