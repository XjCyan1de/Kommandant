package com.github.xjcyan1de.kommandant.tree

import com.github.xjcyan1de.kommandant.node.Argument
import com.github.xjcyan1de.kommandant.node.Literal
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import java.util.function.Predicate


@Suppress("UNCHECKED_CAST")
open class Mapper<T, R> {
    open fun map(command: CommandNode<T>): CommandNode<R> {
        return when (command) {
            is ArgumentCommandNode<*, *> -> argument(command)
            is LiteralCommandNode<*> -> literal(command)
            is RootCommandNode<*> -> root(command)
            else -> otherwise(command)
        }
    }

    open fun argument(command: CommandNode<T>): CommandNode<R> {
        val parameter = command as ArgumentCommandNode<T, *>
        return Argument(parameter.name, type(parameter), command = execution(parameter), requirement = requirement(parameter), suggestions = suggestions(parameter))
    }

    open fun literal(command: CommandNode<T>): CommandNode<R> =
            Literal(command.name, command = execution(command), requirement = requirement(command))

    open fun root(command: CommandNode<T>?): CommandNode<R> = RootCommandNode()

    open fun otherwise(command: CommandNode<T>): CommandNode<R> =
            throw IllegalArgumentException("Unsupported command, '" + command.name + "' of type: " + command.javaClass.name)

    open fun type(command: ArgumentCommandNode<T, *>): ArgumentType<*> = command.type

    open fun execution(command: CommandNode<T>?): Command<R> = NONE as Command<R>

    open fun requirement(command: CommandNode<T>): Predicate<R> = TRUE as Predicate<R>

    open fun suggestions(command: ArgumentCommandNode<T, *>): SuggestionProvider<R>? = null

    companion object {
        val NONE: Command<*> = Command<Any> { 0 }
        val TRUE: Predicate<*> = Predicate<Any> { source -> true }
        val EMPTY: SuggestionProvider<*> = SuggestionProvider { _: CommandContext<Any?>?, builder: SuggestionsBuilder -> builder.buildFuture() }
    }
}