package com.tmiq.config

import com.tmiq.config.categories.CategoryGeneral
import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category

class Config : Config() {

    override fun getTitle(): String {
        return "§aSkybounds§2Addons"
    }

    override fun shouldAutoFocusSearchbar(): Boolean {
        return true
    }

    @Category(name = "General", desc = "")
    var categoryGeneral: CategoryGeneral = CategoryGeneral

}