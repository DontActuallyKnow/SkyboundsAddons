package com.tmiq.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    val name: String = "",
    val description: String = "",
    val aliases: Array<String> = [],
    val usage: String = ""
)
