package com.github.xjcyan1de.kommandant.util

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.ParsedArgument
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CommandContextArguments<T> : ReadOnlyProperty<CommandContext<T>, Map<String, ParsedArgument<T, *>>> {
    private var value: Map<String, ParsedArgument<T, *>>? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: CommandContext<T>, property: KProperty<*>): Map<String, ParsedArgument<T, *>> {
        return value ?: (FIELD.get(thisRef) as Map<String, ParsedArgument<T, *>>).also {
            value = it
        }
    }

    companion object {
        private val FIELD = CommandContext::class.java.getField("arguments").apply {
            isAccessible = true
        }
    }
}