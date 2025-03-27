package com.tmiq.annotations

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation

object CommandRegistry {

    data class CommandData(
        val name: String,
        val aliases: List<String>,
        val description: String,
        val usage: String,
    )

    private val commands = mutableMapOf<String, CommandData>()
    private val aliasToCommand = mutableMapOf<String, String>()

    fun initialize() {
        ClientCommandRegistrationCallback.EVENT.register(
            ClientCommandRegistrationCallback { dispatcher, _ -> registerCommands(dispatcher) }
        )
    }

    private fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        try {
            val reflections = Reflections(
                ConfigurationBuilder()
                    .forPackage("com.tmiq.commands")
                    .setScanners(Scanners.SubTypes, Scanners.TypesAnnotated)
            )

            val allClasses = reflections.getSubTypesOf(Any::class.java).map { it.kotlin }

            val classesWithCommands = allClasses.filter { clazz ->
                clazz.functions.any { it.hasAnnotation<Command>() }
            }

            for (clazz in classesWithCommands) {
                for (function in clazz.functions) {
                    val annotation = function.findAnnotation<Command>() ?: continue
                    registerCommand(dispatcher, function, annotation)
                }
            }

            val commandCount = commands.size
            com.tmiq.SkyboundsAddons.LOGGER.info("Registered $commandCount client commands")

        } catch (e: Exception) {
            // Log any errors during command registration
            com.tmiq.SkyboundsAddons.LOGGER.error("Error registering client commands: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun registerCommand(
        dispatcher: CommandDispatcher<FabricClientCommandSource>,
        function: KFunction<*>,
        annotation: Command
    ) {
        val commandName = annotation.name
        val commandData = CommandData(
            name = commandName,
            aliases = annotation.aliases.toList(),
            description = annotation.description,
            usage = annotation.usage
        )

        // Store command data for later retrieval
        commands[commandName] = commandData

        // Register main command name
        aliasToCommand[commandName] = commandName

        // Register aliases
        annotation.aliases.forEach { alias ->
            aliasToCommand[alias] = commandName
        }

        // Create and register the command
        val command = LiteralArgumentBuilder.literal<FabricClientCommandSource>(commandName)
            .executes { context ->
                val result = try {
                    function.call(context) ?: false
                } catch (e: Exception) {
                    com.tmiq.SkyboundsAddons.LOGGER.error("Error executing command $commandName: ${e.message}")
                    e.printStackTrace()
                    false
                }

                // If the function returns false/0, send usage information
                if (result is Boolean && !result || result is Int && result == 0) {
                    sendUsageMessage(context, commandData)
                    return@executes 0
                }

                // Return 1 for success by default
                if (result is Boolean) if (result) 1 else 0 else (result as? Int) ?: 1
            }

        dispatcher.register(command)

        // Register aliases as separate commands
        for (alias in annotation.aliases) {
            val aliasCommand = LiteralArgumentBuilder.literal<FabricClientCommandSource>(alias)
                .executes { context ->
                    val result = try {
                        function.call(context) ?: false
                    } catch (e: Exception) {
                        com.tmiq.SkyboundsAddons.LOGGER.error("Error executing command $alias: ${e.message}")
                        e.printStackTrace()
                        false
                    }

                    if (result is Boolean && !result || result is Int && result == 0) {
                        sendUsageMessage(context, commandData)
                        return@executes 0
                    }

                    if (result is Boolean) if (result) 1 else 0 else (result as? Int) ?: 1
                }

            dispatcher.register(aliasCommand)
        }

        com.tmiq.SkyboundsAddons.LOGGER.debug("Registered command: $commandName with aliases: ${annotation.aliases.joinToString()}")
    }

    private fun sendUsageMessage(context: CommandContext<FabricClientCommandSource>, commandData: CommandData) {
        context.source.sendFeedback(Text.of(commandData.usage))
    }

    /**
     * Get all registered commands
     */
    fun getAllCommands(): Map<String, CommandData> {
        return commands.toMap()
    }

    /**
     * Get command data by name or alias
     * @param nameOrAlias The command name or any of its aliases
     * @return The command data or null if not found
     */
    fun getCommandByNameOrAlias(nameOrAlias: String): CommandData? {
        val commandName = aliasToCommand[nameOrAlias] ?: return null
        return commands[commandName]
    }
}