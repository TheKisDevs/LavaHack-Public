package com.kisman.cc.file

import com.kisman.cc.Kisman
import com.kisman.cc.friend.FriendManager
import com.kisman.cc.util.ColourUtilKt
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

class ConfigManager(
        val name : String
) {
    val saver : Save = Save(this)
    val loader : Load = Load(this)

    val suffix = ".kis"
    val path = Kisman.fileName

    val modulesPrefix = "module"
    val settingsPrefix = "setting"
    val hudModulesPrefix = "hud_module"
    val friendsPrefix = "friend"


    class Load (
            val config : ConfigManager
    ) {
        @Throws(IOException::class)
        fun init() {
            if(!Files.exists(Paths.get(config.path + config.name + config.suffix))) {
                LoadConfig.init()
                return
            }

            BufferedReader(InputStreamReader(Files.newInputStream(Paths.get(config.path + config.name + config.suffix)))).use { reader ->
                load(reader)
            }
        }

        @Throws(IOException::class)
        fun load(reader : BufferedReader) {
            var line: String?
            while(reader.readLine().also { line = it } != null) {
                val split1 = line?.split("=")
                val split2 = split1?.get(0)?.split(".")

                when(split2?.get(0)) {
                    config.modulesPrefix -> {
                        val module = Kisman.instance.moduleManager.getModule(split2[1])
                        if(module != null) {
                            when(split2[2]) {
                                "toggle" -> {
                                    try {
                                        val toggle = java.lang.Boolean.parseBoolean(split1[1])
                                        if(module.isToggled != toggle) module.isToggled = toggle
                                    } catch (ignored : Exception) {}
                                }
                                "hold" -> {
                                    try {
                                        module.hold = java.lang.Boolean.parseBoolean(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "visible" -> {
                                    try {
                                        module.visible = java.lang.Boolean.parseBoolean(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "key" -> {
                                    try {
                                        module.key = Integer.parseInt(split1[1])
                                    } catch(ignored : Exception) {}
                                }
                                config.settingsPrefix -> {
                                    val setting = Kisman.instance.settingsManager.getSettingByName(module, split2[3])
                                    if(setting != null) {
                                        try {
                                            if(setting.isCheck) setting.valBoolean = java.lang.Boolean.parseBoolean(split1[1])
                                            if(setting.isCombo) setting.valString = split1[1].split("\"")[1]
                                            if(setting.isSlider) setting.valDouble = java.lang.Double.parseDouble(split1[1])
                                            if(setting.isColorPicker) setting.colour = ColourUtilKt.fromConfig(split1[1], setting.colour)
                                        } catch(e : Exception) {}
                                    }
                                }
                            }
                        }
                    }
                    config.hudModulesPrefix -> {
                        val hud = Kisman.instance.hudModuleManager.getModule(split2[1])
                        if(hud != null) {
                            when(split2[2]) {
                                "toggle" -> {
                                    try {
                                        val toggle = java.lang.Boolean.parseBoolean(split1[1])
                                        if(hud.isToggled != toggle) hud.isToggled = toggle
                                    } catch (ignored : Exception) {}
                                }
                                "x" -> {
                                    try {
                                        hud.x = java.lang.Double.parseDouble(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                                "y" -> {
                                    try {
                                        hud.y = java.lang.Double.parseDouble(split1[1])
                                    } catch (ignored : Exception) {}
                                }
                            }
                        }
                    }
                    config.friendsPrefix -> {
                        FriendManager.instance.addFriend(split1[1].split("\"")[1])
                    }
                }
            }

        }
    }

    class Save(
            val config : ConfigManager
    ) {
        @Throws(IOException::class)
        fun init() {
            fileCheck()

            BufferedWriter(
                    FileWriter(
                            Paths.get(config.path + config.name + config.suffix).toFile()
                    )
            ).use { writer ->
                save(writer)
            }
        }

        @Throws(IOException::class)
        private fun save(writer : BufferedWriter) {
            for(module in Kisman.instance.moduleManager.modules) {
                writer.write("${config.modulesPrefix}.${module.name}.toggle=${module.isToggled}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.hold=${module.hold}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.visible=${module.isVisible}")
                writer.newLine()
                writer.write("${config.modulesPrefix}.${module.name}.key=${module.key}")
                writer.newLine()
                if(Kisman.instance.settingsManager.getSettingsByMod(module) != null) {
                    for(setting in Kisman.instance.settingsManager.getSettingsByMod(module)) {
                        if(setting  != null) {
                            if(setting.isCheck) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${setting.valBoolean}")
                                writer.newLine()
                            }
                            if(setting.isCombo) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=\"${setting.valString}\"")
                                writer.newLine()
                            }
                            if(setting.isSlider) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${setting.valDouble}")
                                writer.newLine()
                            }
                            if(setting.isColorPicker) {
                                writer.write("${config.modulesPrefix}.${module.name}.${config.settingsPrefix}.${setting.name}=${ColourUtilKt.toConfig(setting.colour)}")
                                writer.newLine()
                            }
                        }
                    }
                }
            }
            for(hud in Kisman.instance.hudModuleManager.modules) {
                writer.write("${config.hudModulesPrefix}.${hud.name}.toggle=${hud.isToggled}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.x=${hud.x}")
                writer.newLine()
                writer.write("${config.hudModulesPrefix}.${hud.name}.y=${hud.y}")
                writer.newLine()
            }
            if(FriendManager.instance.friends.isNotEmpty()) {
                for(friend in FriendManager.instance.friends) {
                    writer.write("${config.friendsPrefix}=\"$friend\"")
                    writer.newLine()
                }
            }
        }

        @Throws(IOException::class)
        private fun fileCheck() {
            if(Files.exists(Paths.get(config.path + config.name + config.suffix))) {
                File(config.path + config.name + config.suffix).delete()
            } else {
                Files.createFile(Paths.get(config.path + config.name + config.suffix))
            }
        }
    }
}