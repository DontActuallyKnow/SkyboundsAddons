package com.tmiq.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    val name: String = "",
    val description: String = "",
    val aliases: Array<String> = [],
    val usage: String = "",
    val hasSubcommands: Boolean = false
)

@Suppress("unused")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subcommand(
    val name: String,
    val description: String = "",
    val aliases: Array<String> = [],
    val usage: String = "",
    val arguments: Array<String> = []
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Argument(
    val name: String,
    val description: String = "",
    val optional: Boolean = false,
    val defaultValue: String = ""
)