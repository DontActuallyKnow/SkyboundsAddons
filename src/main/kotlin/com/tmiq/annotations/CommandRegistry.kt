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
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

/**
 * A registry for managing and executing commands dynamically in the application.
 *
 * This object provides functionalities to register commands, manage aliases,
 * handle command execution, and retrieve command data by name or alias. Commands
 * are expected to be annotated with the `@Command` annotation, which provides
 * metadata necessary for their registration and usage.
 *
 * Commands must include an `execute` method to handle command logic. Support
 * for aliases and usage descriptions are also provided.
 */
object CommandRegistry {

    /**
     * Represents metadata and execution details for a command.
     *
     * This data class is used to encapsulate information about a command, including:
     * - The primary name of the command.
     * - Any aliases that can be used to invoke the command.
     * - A description of the command's functionality.
     * - Usage instructions for how to properly execute the command.
     * - The actual instance containing the command's execution logic.
     * - The callable method responsible for executing the command.
     *
     * This class is typically used to register, manage, and execute commands in a structured way.
     */
    data class CommandData(
        val name: String,
        val aliases: List<String>,
        val description: String,
        val usage: String,
        val instance: Any,
        val executeMethod: KCallable<*>
    )

    private val commands = mutableMapOf<String, CommandData>()
    private val aliasToCommand = mutableMapOf<String, String>()

    /**
     * Initializes the command registration process for the application.
     *
     * This method sets up a callback to dynamically register commands during the
     * initialization phase. It leverages the `ClientCommandRegistrationCallback` to
     * integrate commands into the dispatcher for the client side. The commands are
     * dynamically discovered and registered based on their annotations and metadata.
     *
     * The registration process automatically identifies classes annotated with the
     * `@Command` annotation, instantiates their instances, and binds their `execute`
     * methods to the appropriate command handler in the dispatcher.
     */
    fun initialize() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            registerCommands(dispatcher)
        }
    }

    /**
     * Registers all commands found in the specified package and annotated with the `@Command` annotation.
     * Each command class must define an `execute` method, which is bound to the command handler.
     *
     * Commands are dynamically discovered via reflection and added to the provided command dispatcher.
     *
     * @param dispatcher The command dispatcher where commands will be registered.
     */
    private fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val reflections = Reflections(
            ConfigurationBuilder()
                .forPackage("com.tmiq.commands")
                .setScanners(Scanners.TypesAnnotated)
        )

        val commandClasses = reflections.getTypesAnnotatedWith(Command::class.java)

        for (commandClass in commandClasses) {
            val annotation = commandClass.getAnnotation(Command::class.java)
            val kClass = commandClass.kotlin

            val instance = kClass.objectInstance ?: try {
                kClass.java.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                println("Failed to instantiate command class ${kClass.qualifiedName}: ${e.message}")
                continue
            }

            val executeMethod = kClass.memberFunctions.find { it.name == "execute" }
            if (executeMethod == null) {
                println("Command class ${kClass.qualifiedName} is missing required 'execute' method")
                continue
            }

            executeMethod.isAccessible = true

            registerCommand(dispatcher, instance, executeMethod, annotation)
        }
    }

    /**
     * Registers a command with the specified dispatcher, using the provided parameters.
     * The command is constructed from metadata specified in the `@Command` annotation
     * and the `executeMethod` function of the given instance. This includes setting up
     * the command's name, aliases, description, and usage information.
     *
     * @param dispatcher The command dispatcher to which the command will be registered.
     * @param instance The instance of the class containing the command execution logic.
     * @param executeMethod The function representing the execution logic of the command.
     * @param annotation The `@Command` annotation containing metadata for the command,
     *                   such as its name, description, aliases, and usage.
     */
    private fun registerCommand(
        dispatcher: CommandDispatcher<FabricClientCommandSource>,
        instance: Any,
        executeMethod: KCallable<*>,
        annotation: Command
    ) {
        val commandData = CommandData(
            name = annotation.name,
            aliases = annotation.aliases.toList(),
            description = annotation.description,
            usage = annotation.usage,
            instance = instance,
            executeMethod = executeMethod
        )

        commands[annotation.name] = commandData

        val commandNode = LiteralArgumentBuilder
            .literal<FabricClientCommandSource>(annotation.name)
            .executes { context ->
                try {
                    val result = executeMethod.call(instance, context) as? Boolean ?: true
                    if (!result) {
                        sendUsageMessage(context, commandData)
                    }
                    1
                } catch (e: Exception) {
                    context.source.sendError(Text.of("An error occurred while executing this command: ${e.message}"))
                    e.printStackTrace()
                    0
                }
            }
            .build()

        dispatcher.root.addChild(commandNode)

        for (alias in annotation.aliases) {
            aliasToCommand[alias] = annotation.name
            val aliasNode = LiteralArgumentBuilder
                .literal<FabricClientCommandSource>(alias)
                .executes { context ->
                    try {
                        val result = executeMethod.call(instance, context) as? Boolean ?: true
                        if (!result) {
                            sendUsageMessage(context, commandData)
                        }
                        1
                    } catch (e: Exception) {
                        context.source.sendError(Text.of("An error occurred while executing this command: ${e.message}"))
                        e.printStackTrace()
                        0
                    }
                }
                .build()

            dispatcher.root.addChild(aliasNode)
        }
    }

    /**
     * Sends a usage message and optional description to the command source.
     *
     * The usage message is derived from the `usage` property of the provided `CommandData`,
     * and the description is shown if it is not empty.
     *
     * @param context The command context containing the sender and other command-related data.
     * @param commandData The metadata of the command, including its usage and description.
     */
    private fun sendUsageMessage(context: CommandContext<FabricClientCommandSource>, commandData: CommandData) {
        context.source.sendFeedback(Text.of("§cUsage: ${commandData.usage}"))
        if (commandData.description.isNotEmpty()) {
            context.source.sendFeedback(Text.of("§7${commandData.description}"))
        }
    }

    /**
     * Retrieves all registered commands in the application.
     *
     * This method returns a mapping of command names to their corresponding
     * `CommandData` objects. Each `CommandData` contains detailed metadata about
     * the command, including its name, aliases, description, usage, and execution logic.
     *
     * @return A map where the keys are command names and the values are `CommandData` objects
     *         representing the details of each registered command.
     */
    fun getAllCommands(): Map<String, CommandData> {
        return commands.toMap()
    }

    /**
     * Retrieves a command by its name or alias.
     *
     * This method checks if the input matches a registered command name or an alias.
     * If a matching command is found by name or resolved via alias mapping, its corresponding
     * `CommandData` is returned. If no match is found, it returns `null`.
     *
     * @param nameOrAlias The name or alias of the command to retrieve.
     * @return The `CommandData` associated with the given name or alias, or `null` if no match is found.
     */
    fun getCommandByNameOrAlias(nameOrAlias: String): CommandData? {
        if (commands.containsKey(nameOrAlias)) {
            return commands[nameOrAlias]
        }

        val commandName = aliasToCommand[nameOrAlias]
        return commandName?.let { commands[it] }
    }
}