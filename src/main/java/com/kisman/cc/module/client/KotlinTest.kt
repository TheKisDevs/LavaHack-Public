package com.kisman.cc.module.client

import com.kisman.cc.module.Category
import com.kisman.cc.module.Module

class KotlinTest(
    name: String = "KotlinTest",
    desc: String = "katlin test!",
    category: Category = Category.CLIENT
) : Module(name, desc, category)