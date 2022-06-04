package com.kisman.cc.util

import java.util.function.Supplier

class LinkedColour(val red: Supplier<Int>, val green: Supplier<Int>, val blue: Supplier<Int>, val alpha: Supplier<Int>) : Colour() {

    constructor(red: Supplier<Int>, green: Supplier<Int>, blue: Supplier<Int>) : this(red, green, blue, Supplier{255})

    fun toColour(): Colour {
        return Colour(red.get(), green.get(), blue.get(), alpha.get())
    }
}