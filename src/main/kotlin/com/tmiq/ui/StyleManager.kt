package com.tmiq.ui

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.awt.Color
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object StyleManager {

    private val configPath = FabricLoader.getInstance().configDir.resolve("skyboundsaddons").toString()
    private val styleFilePath = "$configPath/style.json"
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private var currentStyle: UIStyle = UIStyle()
    private var isDarkMode: Boolean = true

    init {
        loadStyle()
    }

    fun getStyle(): UIStyle {
        return currentStyle
    }

    fun isDarkMode(): Boolean = isDarkMode

    fun toggleDarkMode(): Boolean {
        isDarkMode = !isDarkMode
        return isDarkMode
    }

    fun loadStyle() {
        val configDir = File(configPath)
        if (!configDir.exists()) {
            configDir.mkdirs()
        }

        val styleFile = File(styleFilePath)
        if (!styleFile.exists()) {
            currentStyle = UIStyle()
            saveStyle()
        } else {
            try {
                FileReader(styleFile).use { reader ->
                    currentStyle = gson.fromJson(reader, UIStyle::class.java)
                }
            } catch (e: Exception) {
                println("Error loading style file: ${e.message}")
                currentStyle = UIStyle()
                saveStyle()
            }
        }
    }

    fun saveStyle() {
        try {
            val configDir = File(configPath)
            if (!configDir.exists()) {
                configDir.mkdirs()
            }

            FileWriter(styleFilePath).use { writer ->
                gson.toJson(currentStyle, writer)
            }
        } catch (e: Exception) {
            println("Error saving style file: ${e.message}")
        }
    }

    fun getColor(lightColor: Int, darkColor: Int): Int {
        return if (isDarkMode) darkColor else lightColor
    }
}

// Data classes for style configuration
data class UIStyle(
    val primary: ColorTheme = ColorTheme(
        light = ColorPair("#8E0038", "#FFFFFF"),
        dark = ColorPair("#AF0038", "#FFFFFF")
    ),
    val secondary: ColorTheme = ColorTheme(
        light = ColorPair("#006E8E", "#FFFFFF"),
        dark = ColorPair("#007E8E", "#FFFFFF")
    ),
    val tertiary: ColorTheme = ColorTheme(
        light = ColorPair("#8E6E00", "#FFFFFF"),
        dark = ColorPair("#CDA100", "#FFFFFF")
    ),
    val success: ColorTheme = ColorTheme(
        light = ColorPair("#4CAF50", "#FFFFFF"),
        dark = ColorPair("#4CFF50", "#FFFFFF")
    ),
    val warning: ColorTheme = ColorTheme(
        light = ColorPair("#FF9800", "#FFFFFF"),
        dark = ColorPair("#FFB100", "#FFFFFF")
    ),
    val danger: ColorTheme = ColorTheme(
        light = ColorPair("#F44336", "#FFFFFF"),
        dark = ColorPair("#FF4030", "#FFFFFF")
    ),
    val info: ColorTheme = ColorTheme(
        light = ColorPair("#2196F3", "#FFFFFF"),
        dark = ColorPair("#2196FF", "#FFFFFF")
    ),
    val background: ColorTheme = ColorTheme(
        light = ColorPair("#F0F0F0", "#000000"),
        dark = ColorPair("#191919", "#FFFFFF")
    ),
    val animations: AnimationSettings = AnimationSettings()
)

data class ColorTheme(
    val light: ColorPair,
    val dark: ColorPair
)

data class ColorPair(
    val background: String,
    val foreground: String
) {
    fun getBackgroundColor(): Int = parseHexColor(background)
    fun getForegroundColor(): Int = parseHexColor(foreground)

    private fun parseHexColor(hex: String): Int {
        val colorHex = hex.removePrefix("#")
        return Color(
            Integer.parseInt(colorHex.substring(0, 2), 16),
            Integer.parseInt(colorHex.substring(2, 4), 16),
            Integer.parseInt(colorHex.substring(4, 6), 16)
        ).rgb
    }
}

data class AnimationSettings(
    val hoverTransitionMs: Int = 150,
    val clickTransitionMs: Int = 100,
    val buttonHoverScale: Float = 1.03f,
    val buttonClickScale: Float = 0.97f
)