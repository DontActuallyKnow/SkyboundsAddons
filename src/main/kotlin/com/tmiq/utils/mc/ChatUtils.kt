package com.tmiq.utils.mc

import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object ChatUtils {
    private val mc = MinecraftClient.getInstance()

    // Store last few messages for quick access
    private val recentMessages = ArrayDeque<String>(10)

    /**
     * Sends a chat message to the player
     * @param message The message to send
     */
    fun sendMessage(message: String) {
        mc.inGameHud?.chatHud?.addMessage(Text.literal(message))
    }

    /**
     * Sends a formatted chat message to the player
     * @param message The message to send
     * @param formatting The formatting to apply (color, bold, etc.)
     */
    fun sendMessage(message: String, formatting: Formatting) {
        mc.inGameHud?.chatHud?.addMessage(Text.literal(message).styled { it.withFormatting(formatting) })
    }

    /**
     * Sends a chat message with a specific style
     * @param message The message to send
     * @param style The style to apply
     */
    fun sendStyledMessage(message: String, style: Style) {
        mc.inGameHud?.chatHud?.addMessage(Text.literal(message).setStyle(style))
    }

    /**
     * Sends a formatted chat message with prefix
     * @param prefix The prefix to add before the message
     * @param message The message to send
     * @param prefixFormatting The formatting to apply to the prefix
     * @param messageFormatting The formatting to apply to the message
     */
    fun sendPrefixedMessage(
        prefix: String,
        message: String,
        prefixFormatting: Formatting,
        messageFormatting: Formatting
    ) {
        val prefixText = Text.literal("[$prefix] ").styled { it.withFormatting(prefixFormatting) }
        val messageText = Text.literal(message).styled { it.withFormatting(messageFormatting) }
        mc.inGameHud?.chatHud?.addMessage(prefixText.append(messageText))
    }

    /**
     * Sends an error message to the player (red text)
     * @param message The error message to send
     */
    fun sendError(message: String) {
        sendMessage(message, Formatting.RED)
    }

    /**
     * Sends a success message to the player (green text)
     * @param message The success message to send
     */
    fun sendSuccess(message: String) {
        sendMessage(message, Formatting.GREEN)
    }

    /**
     * Sends a warning message to the player (yellow text)
     * @param message The warning message to send
     */
    fun sendWarning(message: String) {
        sendMessage(message, Formatting.YELLOW)
    }

    /**
     * Sends an info message to the player (aqua text)
     * @param message The info message to send
     */
    fun sendInfo(message: String) {
        sendMessage(message, Formatting.AQUA)
    }

    /**
     * Creates and returns a clickable text component
     * @param message The text to display
     * @param command The command to execute when clicked
     * @param hover The hover text to display (optional)
     * @return The clickable text component
     */
    fun createClickableCommand(message: String, command: String, hover: String? = null): MutableText {
        val text = Text.literal(message)
            .styled { style ->
                style.withClickEvent(
                    net.minecraft.text.ClickEvent(
                        net.minecraft.text.ClickEvent.Action.RUN_COMMAND,
                        command
                    )
                )
                    .withColor(Formatting.YELLOW)
                    .withUnderline(true)

                if (hover != null) {
                    style.withHoverEvent(
                        net.minecraft.text.HoverEvent(
                            net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                            Text.literal(hover)
                        )
                    )
                }

                style
            }
        return text
    }

    /**
     * Creates and returns a copy-to-clipboard text component
     * @param message The text to display
     * @param copyText The text to copy when clicked
     * @param hover The hover text to display (optional)
     * @return The copy-to-clipboard text component
     */
    fun createCopyToClipboard(message: String, copyText: String, hover: String? = "Click to copy"): MutableText {
        val text = Text.literal(message)
            .styled { style ->
                style.withClickEvent(
                    net.minecraft.text.ClickEvent(
                        net.minecraft.text.ClickEvent.Action.COPY_TO_CLIPBOARD,
                        copyText
                    )
                )
                    .withColor(Formatting.AQUA)

                if (hover != null) {
                    style.withHoverEvent(
                        net.minecraft.text.HoverEvent(
                            net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                            Text.literal(hover)
                        )
                    )
                }

                style
            }
        return text
    }

    /**
     * Creates and returns a URL text component
     * @param message The text to display
     * @param url The URL to open when clicked
     * @return The URL text component
     */
    fun createUrlLink(message: String, url: String): MutableText {
        return Text.literal(message)
            .styled { style ->
                style.withClickEvent(
                    net.minecraft.text.ClickEvent(
                        net.minecraft.text.ClickEvent.Action.OPEN_URL,
                        url
                    )
                )
                    .withColor(Formatting.BLUE)
                    .withUnderline(true)
                    .withHoverEvent(
                        net.minecraft.text.HoverEvent(
                            net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                            Text.literal("Click to open URL")
                        )
                    )
            }
    }

    /**
     * Clears the chat
     */
    fun clearChat() {
        mc.inGameHud?.chatHud?.clear(true)
    }

    /**
     * Sends a chat command as the player
     * @param command The command to send (without the leading slash)
     */
    fun sendCommand(command: String) {
        mc.networkHandler?.sendCommand(command)
    }

    /**
     * Checks if a string contains a mention of the player's name
     * @param message The message to check
     * @return True if the message contains the player's name
     */
    fun containsPlayerMention(message: String): Boolean {
        val playerName = mc.player?.name?.string ?: return false
        return message.contains(playerName, ignoreCase = true)
    }

    /**
     * Creates a component with rainbow colored text
     * @param message The text to display with rainbow colors
     * @return The rainbow colored text component
     */
    fun createRainbowText(message: String): MutableText {
        val colors = arrayOf(
            Formatting.RED, Formatting.GOLD, Formatting.YELLOW,
            Formatting.GREEN, Formatting.AQUA, Formatting.BLUE, Formatting.LIGHT_PURPLE
        )

        val result = Text.literal("")

        message.forEachIndexed { index, char ->
            val colorIndex = index % colors.size
            result.append(
                Text.literal(char.toString())
                    .styled { it.withFormatting(colors[colorIndex]) }
            )
        }

        return result
    }

    /**
     * Creates a text component with gradient coloring
     * @param message The text to display with gradient
     * @param startColor The starting color (hex format like "FF0000")
     * @param endColor The ending color (hex format like "0000FF")
     * @return The gradient colored text component
     */
    fun createGradientText(message: String, startColor: String, endColor: String): MutableText {
        val startRgb = Integer.parseInt(startColor, 16)
        val endRgb = Integer.parseInt(endColor, 16)

        val startR = (startRgb shr 16) and 0xFF
        val startG = (startRgb shr 8) and 0xFF
        val startB = startRgb and 0xFF

        val endR = (endRgb shr 16) and 0xFF
        val endG = (endRgb shr 8) and 0xFF
        val endB = endRgb and 0xFF

        val result = Text.literal("")

        message.forEachIndexed { index, char ->
            val ratio = if (message.length > 1) index.toFloat() / (message.length - 1) else 0f

            val r = (startR + (endR - startR) * ratio).toInt()
            val g = (startG + (endG - startG) * ratio).toInt()
            val b = (startB + (endB - startB) * ratio).toInt()

            val rgb = (r shl 16) or (g shl 8) or b

            result.append(
                Text.literal(char.toString())
                    .styled { it.withColor(rgb) }
            )
        }

        return result
    }

    /**
     * Store a recently received chat message
     * @param message The message to store
     */
    fun storeRecentMessage(message: String) {
        recentMessages.addLast(message)
        if (recentMessages.size > 10) {
            recentMessages.removeFirst()
        }
    }

    /**
     * Get the most recent chat message
     * @return The most recent chat message or null if none exists
     */
    fun getLastChatMessage(): String? {
        return recentMessages.lastOrNull()
    }

    /**
     * Get a specific number of recent chat messages
     * @param count The number of messages to retrieve
     * @return A list of recent messages, newest first
     */
    fun getRecentMessages(count: Int = 5): List<String> {
        val result = mutableListOf<String>()
        val size = recentMessages.size

        for (i in 0 until minOf(count, size)) {
            result.add(recentMessages.elementAt(size - 1 - i))
        }

        return result
    }

    /**
     * Strips Minecraft formatting codes from a string
     * @param text The text with formatting codes
     * @return Text without formatting codes
     */
    fun stripFormatting(text: String): String {
        return text.replace("§[a-fA-F0-9klmnor]".toRegex(), "")
    }

    /**
     * Strips all minecraft formatting codes from the text component
     * @param text The Text component to strip formatting from
     * @return Plain string without any formatting
     */
    fun stripFormatting(text: Text): String {
        return stripFormatting(text.string)
    }

    /**
     * Extracts plain text from a formatted chat message
     * @param message The message to process
     * @return The plain text without formatting codes
     */
    fun getPlainText(message: String): String {
        return stripFormatting(message)
    }

    /**
     * Register a chat message listener to be notified of new messages
     * (To be used with a mixin to the chat receiving method)
     */
    private val chatListeners = mutableListOf<(String) -> Unit>()

    /**
     * Add a listener for chat messages
     * @param listener Function that receives the chat message string
     */
    fun addChatListener(listener: (String) -> Unit) {
        chatListeners.add(listener)
    }

    /**
     * Remove a previously registered chat listener
     * @param listener The listener to remove
     */
    fun removeChatListener(listener: (String) -> Unit) {
        chatListeners.remove(listener)
    }

    /**
     * Call this method from a mixin when a chat message is received
     * @param message The received message
     */
    fun onChatMessage(message: String) {
        storeRecentMessage(message)
        chatListeners.forEach { it(message) }
    }

    /**
     * Extracts a player name from a chat message if present
     * Looks for typical formats like <PlayerName> or [PlayerName]
     * @param message The chat message to analyze
     * @return The player name or null if not found
     */
    fun extractPlayerName(message: String): String? {
        val pattern1 = Pattern.compile("<([A-Za-z0-9_]+)>")  // <Player> format
        val pattern2 = Pattern.compile("\\[([A-Za-z0-9_]+)\\]") // [Player] format

        val matcher1 = pattern1.matcher(message)
        if (matcher1.find()) {
            return matcher1.group(1)
        }

        val matcher2 = pattern2.matcher(message)
        if (matcher2.find()) {
            return matcher2.group(1)
        }

        return null
    }

    /**
     * Processes a chat component and applies custom actions based on its content
     * @param text The text component to process
     * @param onPlayerName Action to take when a player name is found (receives the name)
     * @param onItemName Action to take when an item name is found (receives the item name)
     * @param onCommand Action to take when a command is found (receives the command)
     * @param onUrl Action to take when a URL is found (receives the URL)
     * @return The processed text component
     */
    fun processChatComponent(
        text: MutableText,
        onPlayerName: ((String) -> Unit)? = null,
        onItemName: ((String) -> Unit)? = null,
        onCommand: ((String) -> Unit)? = null,
        onUrl: ((String) -> Unit)? = null
    ): MutableText {
        val plainText = text.string

        // Process player names
        if (onPlayerName != null) {
            val playerName = extractPlayerName(plainText)
            if (playerName != null) {
                onPlayerName(playerName)
            }
        }

        // Basic URL detection and processing
        if (onUrl != null) {
            val urlPattern = Pattern.compile("(https?://[\\w\\-\\.]+\\.[a-zA-Z]{2,}(?:/[^\\s]*)?)")
            val matcher = urlPattern.matcher(plainText)
            if (matcher.find()) {
                onUrl(matcher.group(1))
            }
        }

        // Command detection (starts with /)
        if (onCommand != null && plainText.trim().startsWith("/")) {
            val command = plainText.trim().substringBefore(" ").substring(1)
            onCommand(command)
        }

        return text
    }
}