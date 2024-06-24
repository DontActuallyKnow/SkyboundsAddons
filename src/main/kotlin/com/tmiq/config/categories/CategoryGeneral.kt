package com.tmiq.config.categories

//Todo: Implement and port MoulConfig

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorColour
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CategoryGeneral {

    @JvmField @Expose
    @ConfigOption(name = "Dev", desc = "Enable/disable dev mode") @ConfigEditorBoolean
     var isDevMode: Boolean = false

    @JvmField @Expose
    @ConfigOption(name = "Test Color picker", desc = "Test for color picker") @ConfigEditorColour
    var testColor: String = "0:0:0:0:0"

}