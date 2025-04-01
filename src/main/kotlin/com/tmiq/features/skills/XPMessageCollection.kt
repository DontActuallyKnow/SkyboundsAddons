package com.tmiq.features.skills

import com.tmiq.utils.Utils
import com.tmiq.utils.mc.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.lang.reflect.Method
import java.time.Instant
import java.util.regex.Pattern

class XPMessageCollection {

    private val mc = MinecraftClient.getInstance()
    private val xpPattern = Pattern.compile("\\[\\+] \\+ (\\d+) Player Level Xp") // I LOVE AI SO MUCH

    private val collectedXp = mutableListOf<Int>()
    private var lastXpTime = Instant.now().minusSeconds(61)
    private var totalXp = 0
    private var lastMessageId: Text? = null
    private var refreshMethod: Method? = null

    init {
        try {
            refreshMethod = mc.inGameHud.chatHud.javaClass.getDeclaredMethod("refresh")
            refreshMethod?.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun initListener() {
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val messageString = message.string
            val handled = processMessage(messageString, message)

            !handled
        }
    }

    private fun processMessage(message: String, originalText: Text): Boolean {
        val cleanMessage = ChatUtils.stripFormatting(message)

        val matcher = xpPattern.matcher(cleanMessage)
        if (!matcher.find()) return false

        val xpValue = matcher.group(1).toIntOrNull() ?: return false
        val currentTime = Instant.now()

        if (currentTime.isAfter(lastXpTime.plusSeconds(60))) {
            collectedXp.clear()
            totalXp = xpValue
            collectedXp.add(totalXp)
            lastXpTime = currentTime
            lastMessageId = null
            return false
        } else {
            collectedXp.add(xpValue)
            totalXp += xpValue
            lastXpTime = currentTime

            sendCombinedXpMessage()
            return true
        }
    }

    private fun sendCombinedXpMessage() {
        if (lastMessageId != null) {
            clearPreviousXpMessage()
        }

        val baseMessage = Utils.translateChat("&8&l[&b+] &r&a+ &r&7$totalXp &r&bPlayer Level Xp ")

        val hoverPart = Utils.translateChat("&7&l[&r&7++&l]&r")
            .setStyle(Style.EMPTY.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, createHoverText())))

        val fullMessage = baseMessage.append(hoverPart)
        lastMessageId = fullMessage

        // Add the new message to chat
        mc.inGameHud.chatHud.addMessage(fullMessage)
    }

    private fun clearPreviousXpMessage() {
        try {
            val chatHudAccessor = mc.inGameHud.chatHud
            val visibleMessages = chatHudAccessor.javaClass.getDeclaredField("visibleMessages")
            visibleMessages.isAccessible = true

            val messages = visibleMessages.get(chatHudAccessor) as MutableList<*>

            val iterator = messages.iterator()
            while (iterator.hasNext()) {
                val line = iterator.next()
                if (line is ChatHudLine) {
                    val content = line.content
                    if (content.string.contains("Player Level Xp")) {
                        iterator.remove()
                        break
                    }
                }
            }

            val messagesField = chatHudAccessor.javaClass.getDeclaredField("messages")
            messagesField.isAccessible = true
            val allMessages = messagesField.get(chatHudAccessor) as MutableList<*>

            val allIterator = allMessages.iterator()
            while (allIterator.hasNext()) {
                val line = allIterator.next()
                if (line is ChatHudLine) {
                    val content = line.content
                    if (content.string.contains("Player Level Xp")) {
                        allIterator.remove()
                        break
                    }
                }
            }

            refreshMethod?.invoke(chatHudAccessor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createHoverText(): Text {
        val hoverText = Text.literal("Collected XP values:\n")
            .setStyle(Style.EMPTY.withColor(Formatting.GOLD))

        collectedXp.forEachIndexed { index, xp ->
            hoverText.append(
                Text.literal("• $xp XP")
                    .setStyle(Style.EMPTY.withColor(Formatting.WHITE))
            )

            if (index < collectedXp.size - 1) {
                hoverText.append(Text.literal("\n"))
            }
        }

        return hoverText
    }

    companion object {
        private val instance = XPMessageCollection()

        fun getInstance(): XPMessageCollection {
            return instance
        }
    }

}