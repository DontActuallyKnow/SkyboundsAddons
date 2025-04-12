package com.tmiq.annotations.processors

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.tmiq.annotations.Argument
import com.tmiq.annotations.Command
import com.tmiq.annotations.Subcommand
import com.tmiq.utils.Utils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

object CommandRegistry {

    data class CommandData(
        val name: String,
        val aliases: List<String>,
        val description: String,
        val usage: String,
        val instance: Any,
        val executeMethod: KCallable<*>,
        val hasSubcommands: Boolean = false,
        val subcommands: MutableList<SubcommandData> = mutableListOf()
    )

    data class SubcommandData(
        val name: String,
        val aliases: List<String>,
        val description: String,
        val usage: String,
        val parentCommand: String,
        val executeMethod: KFunction<*>,
        val arguments: List<ArgumentData> = emptyList()
    )

    data class ArgumentData(
        val name: String,
        val description: String,
        val optional: Boolean,
        val defaultValue: String,
        val type: KClass<*>
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

            registerHelpCommand(dispatcher)
        }
    }

    private fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val reflections = Reflections(
            ConfigurationBuilder()
                .forPackage("com.tmiq.commands")
                .setScanners(Scanners.TypesAnnotated)
        )

        val commandClasses = reflections.get(
            Scanners.TypesAnnotated.with(Command::class.java).asClass<Any>()
        )

        for (commandClass in commandClasses) {
            try {
                val instance = if (commandClass.kotlin.objectInstance != null) {
                    commandClass.kotlin.objectInstance!!
                } else {
                    commandClass.getDeclaredConstructor().newInstance()
                }

                val commandAnnotation = commandClass.getAnnotation(Command::class.java)
                val name = commandAnnotation.name.ifEmpty { commandClass.simpleName.lowercase() }
                val executeMethod = commandClass.kotlin.memberFunctions.find { it.name == "execute" }

                if (executeMethod != null) {
                    executeMethod.isAccessible = true
                    registerCommand(
                        dispatcher,
                        name,
                        commandAnnotation.description,
                        commandAnnotation.aliases.toList(),
                        commandAnnotation.usage,
                        instance,
                        executeMethod,
                        commandAnnotation.hasSubcommands
                    )
                } else {
                    println("Command $name has no execute method")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun registerCommand(
        dispatcher: CommandDispatcher<FabricClientCommandSource>,
        name: String,
        description: String,
        aliases: List<String>,
        usage: String,
        instance: Any,
        executeMethod: KCallable<*>,
        hasSubcommands: Boolean
    ) {
        val commandBuilder = LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)

        // Register the main command execution
        commandBuilder.executes { context ->
            executeMethod.call(instance, context) as? Int ?: 1
        }

        // Create CommandData object to store command information
        val commandData = CommandData(
            name = name,
            aliases = aliases,
            description = description,
            usage = usage,
            instance = instance,
            executeMethod = executeMethod,
            hasSubcommands = hasSubcommands
        )

        // If has subcommands, register them
        if (hasSubcommands) {
            val clazz = instance::class
            clazz.memberFunctions.forEach { function ->
                val subcommandAnnotation = function.findAnnotation<Subcommand>()
                if (subcommandAnnotation != null) {
                    val subCommandName = subcommandAnnotation.name
                    val subCommandBuilder = LiteralArgumentBuilder.literal<FabricClientCommandSource>(subCommandName)

                    // Process arguments for this subcommand
                    val arguments = function.parameters
                        .filter { it.hasAnnotation<Argument>() }
                        .map { param ->
                            val argAnnotation = param.findAnnotation<Argument>()!!
                            ArgumentData(
                                name = argAnnotation.name,
                                description = argAnnotation.description,
                                optional = argAnnotation.optional,
                                defaultValue = argAnnotation.defaultValue,
                                type = param.type.classifier as KClass<*>
                            )
                        }

                    // Store subcommand data
                    val subcommandData = SubcommandData(
                        name = subCommandName,
                        aliases = subcommandAnnotation.aliases.toList(),
                        description = subcommandAnnotation.description,
                        usage = subcommandAnnotation.usage,
                        parentCommand = name,
                        executeMethod = function,
                        arguments = arguments
                    )

                    commandData.subcommands.add(subcommandData)

                    // If no arguments, directly execute the function
                    if (arguments.isEmpty()) {
                        subCommandBuilder.executes { context ->
                            function.call(instance, context) as? Int ?: 1
                        }
                    } else {
                        // Handle arguments with a recursive approach
                        buildArgumentTree(
                            subCommandBuilder,
                            arguments,
                            function,
                            instance
                        )
                    }

                    // Add subcommand to main command
                    commandBuilder.then(subCommandBuilder)

                    // Register aliases for subcommand
                    subcommandAnnotation.aliases.forEach { alias ->
                        val aliasBuilder = LiteralArgumentBuilder.literal<FabricClientCommandSource>(alias)
                        // Configure alias builder similarly
                        if (arguments.isEmpty()) {
                            aliasBuilder.executes { context ->
                                function.call(instance, context) as? Int ?: 1
                            }
                        } else {
                            buildArgumentTree(
                                aliasBuilder,
                                arguments,
                                function,
                                instance
                            )
                        }
                        commandBuilder.then(aliasBuilder)
                    }
                }
            }
        }

        // Register the command
        dispatcher.register(commandBuilder)

        // Store command data
        commands[name] = commandData

        // Register aliases
        aliases.forEach { alias ->
            val aliasBuilder = LiteralArgumentBuilder.literal<FabricClientCommandSource>(alias)
            // Configure alias similarly to main command
            aliasBuilder.executes { context ->
                executeMethod.call(instance, context) as? Int ?: 1
            }

            // If has subcommands, add them to the alias as well
            if (hasSubcommands) {
                commandData.subcommands.forEach { subcommand ->
                    val subCommandBuilder = LiteralArgumentBuilder.literal<FabricClientCommandSource>(subcommand.name)

                    if (subcommand.arguments.isEmpty()) {
                        subCommandBuilder.executes { context ->
                            subcommand.executeMethod.call(instance, context) as? Int ?: 1
                        }
                    } else {
                        buildArgumentTree(
                            subCommandBuilder,
                            subcommand.arguments,
                            subcommand.executeMethod,
                            instance
                        )
                    }

                    aliasBuilder.then(subCommandBuilder)
                }
            }

            dispatcher.register(aliasBuilder)
            aliasToCommand[alias] = name
        }
    }

    private fun buildArgumentTree(
        builder: LiteralArgumentBuilder<FabricClientCommandSource>,
        arguments: List<ArgumentData>,
        function: KFunction<*>,
        instance: Any,
        currentIndex: Int = 0,
        collectedArgs: MutableMap<String, Any> = mutableMapOf()
    ) {
        if (currentIndex >= arguments.size) {
            // We've processed all arguments, now execute the command
            builder.executes { context ->
                // Build the list of arguments to pass to the function
                val args = mutableListOf<Any?>(instance, context)

                // Find the CommandContext parameter to avoid adding it twice
                val contextParamIndex = function.parameters.indexOfFirst {
                    it.type.classifier == CommandContext::class
                }

                function.parameters.forEachIndexed { index, param ->
                    if (index > 0 && index != contextParamIndex) {
                        val argAnnotation = param.findAnnotation<Argument>()
                        if (argAnnotation != null) {
                            args.add(collectedArgs[argAnnotation.name] ?:
                            if (argAnnotation.optional) convertDefault(argAnnotation.defaultValue, param.type.classifier as KClass<*>)
                            else null)
                        }
                    }
                }

                function.call(*args.toTypedArray()) as? Int ?: 1
            }
            return
        }

        val argument = arguments[currentIndex]
        val argumentBuilder = createArgumentBuilder(argument)

        // For optional arguments, we need both paths - with and without the argument
        if (argument.optional) {
            // Path without the optional argument
            buildArgumentTree(
                builder,
                arguments,
                function,
                instance,
                currentIndex + 1,
                collectedArgs
            )
        }

        // Path with the argument
        argumentBuilder.executes { context ->
            val value = getArgumentValue(context, argument)
            collectedArgs[argument.name] = value

            // If this is the last argument, execute the function
            if (currentIndex == arguments.size - 1) {
                // Similar execution code as above
                val args = mutableListOf<Any?>(instance, context)

                val contextParamIndex = function.parameters.indexOfFirst {
                    it.type.classifier == CommandContext::class
                }

                function.parameters.forEachIndexed { index, param ->
                    if (index > 0 && index != contextParamIndex) {
                        val argAnnotation = param.findAnnotation<Argument>()
                        if (argAnnotation != null) {
                            args.add(collectedArgs[argAnnotation.name] ?:
                            if (argAnnotation.optional) convertDefault(argAnnotation.defaultValue, param.type.classifier as KClass<*>)
                            else null)
                        }
                    }
                }

                return@executes function.call(*args.toTypedArray()) as? Int ?: 1
            }

            1 // Success
        }

        // Continue building for the next arguments
        buildArgumentTree(
            builder.then(argumentBuilder) as LiteralArgumentBuilder<FabricClientCommandSource>,
            arguments,
            function,
            instance,
            currentIndex + 1,
            collectedArgs
        )
    }

    private fun createArgumentBuilder(argument: ArgumentData): RequiredArgumentBuilder<FabricClientCommandSource, *> {
        return when (argument.type) {
            Int::class -> RequiredArgumentBuilder.argument(
                argument.name,
                IntegerArgumentType.integer()
            )
            Double::class, Float::class -> RequiredArgumentBuilder.argument(
                argument.name,
                IntegerArgumentType.integer() // Replace with appropriate type
            )
            else -> RequiredArgumentBuilder.argument(
                argument.name,
                StringArgumentType.string()
            )
        }
    }

    private fun getArgumentValue(context: CommandContext<FabricClientCommandSource>, argument: ArgumentData): Any {
        return when (argument.type) {
            Int::class -> IntegerArgumentType.getInteger(context, argument.name)
            Double::class -> IntegerArgumentType.getInteger(context, argument.name).toDouble() // Replace with appropriate getter
            Float::class -> IntegerArgumentType.getInteger(context, argument.name).toFloat() // Replace with appropriate getter
            else -> StringArgumentType.getString(context, argument.name)
        }
    }

    private fun convertDefault(defaultValue: String, type: KClass<*>): Any? {
        return when (type) {
            Int::class -> defaultValue.toIntOrNull()
            Double::class -> defaultValue.toDoubleOrNull()
            Float::class -> defaultValue.toFloatOrNull()
            Boolean::class -> defaultValue.toBoolean()
            else -> defaultValue
        }
    }

    private fun registerHelpCommand(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val helpBuilder = LiteralArgumentBuilder.literal<FabricClientCommandSource>("help")

        // /help - show all commands
        helpBuilder.executes { context ->
            context.source.sendFeedback(Utils.translateChat("&6Available commands:"))

            commands.values.forEach { command ->
                context.source.sendFeedback(Utils.translateChat("&7/${command.name} - ${command.description}"))
            }

            context.source.sendFeedback(Utils.translateChat("&7Use /help <command> for more information about a specific command."))
            1
        }

        // /help <command> - show specific command help
        val commandArgBuilder = RequiredArgumentBuilder.argument<FabricClientCommandSource, String>("command", StringArgumentType.word())
        commandArgBuilder.executes { context ->
            val commandName = StringArgumentType.getString(context, "command")
            val command = commands[commandName] ?: commands.values.find { commandName in it.aliases }

            if (command != null) {
                context.source.sendFeedback(Utils.translateChat("&6Command: &e/${command.name}"))
                context.source.sendFeedback(Utils.translateChat("&6Description: &7${command.description}"))

                if (command.aliases.isNotEmpty()) {
                    context.source.sendFeedback(Utils.translateChat("&6Aliases: &7${command.aliases.joinToString(", ") { "/$it" }}"))
                }

                context.source.sendFeedback(Utils.translateChat("&6Usage: &7${command.usage}"))

                if (command.hasSubcommands && command.subcommands.isNotEmpty()) {
                    context.source.sendFeedback(Utils.translateChat("&6Subcommands:"))

                    command.subcommands.forEach { subcommand ->
                        val usageText = if (subcommand.usage.isNotEmpty()) {
                            subcommand.usage
                        } else {
                            buildUsageFromArguments("/${command.name} ${subcommand.name}", subcommand.arguments)
                        }

                        context.source.sendFeedback(Utils.translateChat("&7  ${subcommand.name} - ${subcommand.description}"))
                        context.source.sendFeedback(Utils.translateChat("&7  Usage: $usageText"))
                    }
                }
            } else {
                context.source.sendFeedback(Utils.translateChat("&cCommand not found: $commandName"))
            }

            1
        }

        helpBuilder.then(commandArgBuilder)
        dispatcher.register(helpBuilder)
    }

    private fun buildUsageFromArguments(baseCommand: String, arguments: List<ArgumentData>): String {
        val argParts = arguments.map { arg ->
            if (arg.optional) "[${arg.name}]" else "<${arg.name}>"
        }

        return if (argParts.isEmpty()) baseCommand else "$baseCommand ${argParts.joinToString(" ")}"
    }

    fun getCommandByName(name: String): CommandData? {
        return commands[name] ?: commands[aliasToCommand[name]]
    }
}
