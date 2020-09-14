package com.github.xjcyan1de.kommandant

import com.github.xjcyan1de.kommandant.util.CommandContextArguments
import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.StringRange
import kotlin.reflect.KClass

class OptionalContext<T>(
        val context: CommandContext<T>
) : CommandContext<T>(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false
) {
    private val arguments by CommandContextArguments()

    override fun <V> getArgument(name: String, type: Class<V>): V? =
            if (arguments.containsKey(name)) context.getArgument(name, type) else null

    fun <V : Any> getArgument(name: String, type: KClass<V>): V? = getArgument(name, type.java)
    inline fun <reified V : Any> getArgument(name: String): V? = getArgument(name, V::class.java)

    fun <V> getArgument(name: String, type: Class<V>, value: V): V = getArgument(name, type) ?: value
    fun <V : Any> getArgument(name: String, type: KClass<V>, value: V): V = getArgument(name, type.java) ?: value
    inline fun <reified V : Any> getArgument(name: String, value: V): V = getArgument(name, V::class.java) ?: value

    override fun getChild(): CommandContext<T> = context.child
    override fun getLastChild(): CommandContext<T> = context.lastChild
    override fun getCommand(): Command<T> = context.command
    override fun getSource(): T = context.source
    override fun getRedirectModifier(): RedirectModifier<T> = context.redirectModifier
    override fun getRange(): StringRange = context.range
    override fun getInput(): String = context.input
    override fun isForked(): Boolean = context.isForked
}