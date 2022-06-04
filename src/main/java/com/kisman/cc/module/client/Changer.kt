package com.kisman.cc.module.client

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.gui.csgo.components.Slider
import com.kisman.cc.module.Category
import com.kisman.cc.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Colour
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.server.SPacketTimeUpdate

class Changer : Module("Changer", "FullBright + CustomFov + Ambience + CustomTime", Category.CLIENT) {
    private val gamma = Setting("Gamma", this, 100.0, 1.0, 100.0, true)
    private val fov = Setting("Fov", this, 120.0, 30.0, 150.0, true)
    val ambience = Setting("Ambience", this, false)

    //Ambience settings
    val ambColor: Setting = Setting("Ambience Color", this, "Ambience Color", Colour(-1)).setVisible { ambience.valBoolean }

    private val time = Setting("Time", this, false)

    //Time settings
    private val timeVal = Setting("Time Value", this, 24.0, 5.0, 25.0, true).setVisible { time.valBoolean }
    private val timeInfCircle = Setting("Time Infinity Circle", this, true).setVisible { time.valBoolean }
    private val timeSpeed = Setting("Time Speed", this, 100.0, 10.0, 1000.0, Slider.NumberType.TIME).setVisible { time.valBoolean }

    var circle = 0
    private var oldFov = 0F

    init {
        setmgr.rSetting(gamma)
        setmgr.rSetting(fov)
        setmgr.rSetting(ambience)
        setmgr.rSetting(ambColor)
        setmgr.rSetting(time)
        setmgr.rSetting(timeVal)
        setmgr.rSetting(timeInfCircle)
        setmgr.rSetting(timeSpeed)
    }

    override fun onEnable() {
        Kisman.EVENT_BUS.subscribe(receive)
        oldFov = mc.gameSettings.fovSetting
    }

    override fun onDisable() {
        Kisman.EVENT_BUS.unsubscribe(receive)
        mc.gameSettings.gammaSetting = 1F
        mc.gameSettings.fovSetting = oldFov
    }

    override fun update() {
        if(mc.player == null || mc.world == null) return

        mc.gameSettings.gammaSetting = gamma.valFloat
        mc.gameSettings.fovSetting = fov.valFloat

        if(time.valBoolean) {
            circle += timeSpeed.valInt
            mc.world.worldTime = if (timeInfCircle.valBoolean) circle.toLong() else timeVal.valLong * 1000L
            if (circle >= 24000) circle = 0
        }
    }

    val receive = Listener<PacketEvent.Receive>(EventHook {
        if(time.valBoolean && it.packet is SPacketTimeUpdate) {
            it.cancel()
        }
    })
}