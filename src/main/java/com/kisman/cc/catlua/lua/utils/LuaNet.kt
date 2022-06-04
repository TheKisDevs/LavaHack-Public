package com.kisman.cc.catlua.lua.utils

import com.kisman.cc.api.util.PasteBinAPI
import com.kisman.cc.util.process.web.util.HttpTools

class LuaNet {
    fun getPasteBinAPI(url: String): PasteBinAPI {
        return PasteBinAPI(url)
    }

    fun ping(url: String): Boolean {
        return HttpTools.ping(url)
    }

    companion object {
        var instance = LuaNet()

        fun getDefault(): LuaNet {
            if(instance == null) instance = LuaNet()
            return instance
        }
    }
}
