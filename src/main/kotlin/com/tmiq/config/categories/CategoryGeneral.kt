package com.tmiq.config.categories

import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CategoryGeneral {

    @ConfigOption(name = "Dev", desc = "Enable/disable dev mode")
    @ConfigEditorBoolean
    var isDevMode: Boolean = false

}