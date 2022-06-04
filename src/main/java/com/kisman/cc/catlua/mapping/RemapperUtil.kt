package com.kisman.cc.catlua.mapping

import com.kisman.cc.Kisman

class RemapperUtil {
    companion object {
        fun canBeRemapped(path: String): Boolean {
            for(exclude in Kisman.instance.excludedList.list) if(path.contains(exclude)) return false
            return true
        }
    }
}