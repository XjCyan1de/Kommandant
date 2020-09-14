package com.github.xjcyan1de.kommandant.node

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import java.util.function.Predicate

class Argument<T,V>(
        name: String,
        type: ArgumentType<V>,
        command: Command<T>,
        requirement: Predicate<T>,
        destination: CommandNode<T>? = null,
        modifier: RedirectModifier<T>? = null,
        fork: Boolean = false,
        suggestions: SuggestionProvider<T>? = null
) : ArgumentCommandNode<T, V>(
    name, type, command, requirement, destination, modifier, fork, suggestions
) {
    class Builder<T, V>(
            var name: String? = null,
            var type: ArgumentType<V>? = null,
            var suggestions: SuggestionProvider<T>? = null
    ) : NodeBuilder<T, Builder<T, V>>() {
        fun name(name: String) = apply {
            this.name = name
        }

        fun type(type: ArgumentType<V>) = apply {
            this.type = type
        }

        fun suggests(suggestions: SuggestionProvider<T>) = apply {
            this.suggestions = suggestions
        }

        override fun getThis(): Builder<T, V> = this

        override fun build(): Argument<T,V> {
            val argument = Argument(
                    requireNotNull(name),
                    requireNotNull(type),
                    command, requirement, redirect, redirectModifier, isFork,
                    requireNotNull(suggestions)
            )
            arguments.forEach {
                argument.addChild(it)
            }
            return argument
        }
    }

    companion object {
        @JvmStatic
        fun <T,V> of(name: String, type: ArgumentType<V>): Builder<T, V> = Builder(name, type)
    }
}

fun <T, V> Argument(name: String, type: ArgumentType<V>, block: Argument.Builder<T,V>.() -> Unit = {}) =
        Argument.of<T,V>(name, type).apply(block).build()