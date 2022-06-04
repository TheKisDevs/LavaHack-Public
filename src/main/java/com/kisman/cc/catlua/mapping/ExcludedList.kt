package com.kisman.cc.catlua.mapping

import com.kisman.cc.api.util.PasteBinAPI
import com.kisman.cc.api.util.exception.PasteBinBufferedReaderException

class ExcludedList {
    val url = "https://pastebin.com/raw/nbrZLTg3"
    var list: List<String> = ArrayList()

    init {
        try {

            list = PasteBinAPI(url).get()
        } catch(ignored: PasteBinBufferedReaderException) {}
    }
}