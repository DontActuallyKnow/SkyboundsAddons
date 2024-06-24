package com.tmiq.config

//Todo: Implement and port MoulConfig

import com.google.gson.annotations.Expose
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

    @JvmField @Expose @Category(name = "General", desc = "")
    var categoryGeneral: CategoryGeneral = CategoryGeneral()

}
