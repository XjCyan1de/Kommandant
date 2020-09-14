package com.github.xjcyan1de.kommandant.node

import com.github.xjcyan1de.kommandant.Mutable
import com.github.xjcyan1de.kommandant.util.add
import com.github.xjcyan1de.kommandant.util.execution
import com.github.xjcyan1de.kommandant.util.remove
import com.mojang.brigadier.Command
import com.mojang.brigadier.Message
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import java.util.function.Predicate

class Argument<T,V>(
        name: String,
        type: ArgumentType<V>,
        command: Command<T>,
        requirement: Predicate<T>,
        var destination: CommandNode<T>? = null,
        modifier: RedirectModifier<T>? = null,
        fork: Boolean = false,
        suggestions: SuggestionProvider<T>? = null
) : ArgumentCommandNode<T, V> (
    name, type, command, requirement, destination, modifier, fork, suggestions
), Mutable<T> {
    override fun setCommand(command: Command<T>) {
        execution(command)
    }

    override fun addChild(child: CommandNode<T>) {
        add(child) {
            super.addChild(child)
        }
    }

    override fun removeChild(child: String): CommandNode<T>? = remove(child)

    override fun getRedirect(): CommandNode<T>? = destination

    override fun setRedirect(destination: CommandNode<T>?) {
        this.destination = destination
    }

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

        fun suggests(block: SuggestionsBuilder.(CommandContext<T>)->Unit) = apply {
            suggests { context: CommandContext<T>, builder ->
                block(builder, context)
                builder.buildFuture()
            }
        }

        fun suggests(vararg values: String) = suggests(values.asIterable())
        fun suggests(values: Iterable<String>) = apply {
            suggests {
                values.forEach {
                    suggest(it)
                }
            }
        }

        @JvmName("suggestsWithMessage")
        fun suggests(vararg values: Pair<String, Message>) = suggests(values.asIterable())
        @JvmName("suggestsWithMessage")
        fun suggests(values: Iterable<Pair<String, Message>>)  = apply {
            suggests {
                values.forEach {
                    suggest(it.first, it.second)
                }
            }
        }

        @JvmName("suggestsWithTooltips")
        fun suggests(vararg values: Pair<String, String>) = suggests(values.asIterable())
        @JvmName("suggestsWithTooltips")
        fun suggests(values: Iterable<Pair<String, String>>)  = apply {
            suggests {
                values.forEach {
                    suggest(it.first) {
                        it.second
                    }
                }
            }
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