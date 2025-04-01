package com.tmiq.config

import com.tmiq.SkyboundsAddons
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import net.fabricmc.loader.api.FabricLoader

@Suppress("unused")
open class Config {

    companion object {
        @JvmField
        val GSON: ConfigClassHandler<Config> = ConfigClassHandler.createBuilder(Config::class.java)
            .serializer { config ->
                GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().configDir.resolve(SkyboundsAddons.MOD_ID + "/config.json"))
                    .build()
            }
            .build()
    }

    var modEnabled: Boolean = false

//    @SerialEntry
//    var booleanToggle = false
//
//    @SerialEntry
//    var customBooleanToggle = false
//
//    @SerialEntry
//    var tickbox = false
//
//    @SerialEntry
//    var intSlider = 0
//
//    @SerialEntry
//    var doubleSlider = 0.0
//
//    @SerialEntry
//    var floatSlider = 0f
//
//    @SerialEntry
//    var longSlider = 0L
//
//    @SerialEntry
//    var textField = "Hello"
//
//    @SerialEntry
//    var colorOption: Color = Color.red
//
//    @SerialEntry
//    var topColorOption: Color = Color.blue
//
//    @SerialEntry
//    var alphaColorOption: Color = Color.green
//
//    @SerialEntry
//    var buttonColorOption: Color = Color(0, 255, 255)
//
//    @SerialEntry
//    var alternativePreviewOutline: Color = Color.white
//
//    @SerialEntry
//    var anotherAlphaColorOption = Color(3, 24, 255, 158)
//
//    @SerialEntry
//    var doubleField = 0.5
//
//    @SerialEntry
//    var floatField = 0.5f
//
//    @SerialEntry
//    var intField = 5
//
//    @SerialEntry
//    var longField = 5L
//
//    @SerialEntry
//    var enumOption = Alphabet.A
//
//    @SerialEntry
//    var stringOptions = "Banana"
//
//    @SerialEntry
//    var stringSuggestions = ""
//
//    @SerialEntry
//    var item: Item = Items.OAK_LOG
//
//    @SerialEntry
//    var stringList =
//        listOf("This is quite cool.", "You can add multiple items!", "And it is integrated so well into Option groups!")
//
//    @SerialEntry
//    var intList = listOf(1, 2, 3)
//
//    @SerialEntry
//    var groupTestRoot = false
//
//    @SerialEntry
//    var groupTestFirstGroup = false
//
//    @SerialEntry
//    var groupTestFirstGroup2 = false
//
//    @SerialEntry
//    var groupTestSecondGroup = false
//
//    @SerialEntry
//    var scrollingSlider = 0
//
//    enum class Alphabet {
//        A, B, C
//    }

}