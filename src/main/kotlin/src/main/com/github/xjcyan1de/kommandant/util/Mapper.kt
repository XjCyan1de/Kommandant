package src.main.com.github.xjcyan1de.kommandant.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import src.main.com.github.xjcyan1de.kommandant.node.Argument
import src.main.com.github.xjcyan1de.kommandant.node.Literal
import java.util.function.Predicate

open class Mapper<T, R> {
    fun map(command: CommandNode<T>): CommandNode<R> {
        return if (command is ArgumentCommandNode<*, *>) {
            argument(command)
        } else if (command is LiteralCommandNode<*>) {
            literal(command)
        } else if (command is RootCommandNode<*>) {
            root(command)
        } else {
            otherwise(command)
        }
    }

    protected fun argument(command: CommandNode<T>): CommandNode<R> {
        val parameter = command as ArgumentCommandNode<T, *>
        return Argument(parameter.name, type(parameter) as ArgumentType<Any>, execution(parameter), requirement(parameter), suggestions(parameter))
    }

    protected fun literal(command: CommandNode<T>): CommandNode<R> {
        return Literal(command.name, execution(command), requirement(command))
    }

    protected fun root(command: CommandNode<T>?): CommandNode<R> {
        return RootCommandNode()
    }

    protected fun otherwise(command: CommandNode<T>): CommandNode<R> {
        throw IllegalArgumentException("Unsupported command, '" + command.name + "' of type: " + command.javaClass.name)
    }

    protected open fun type(command: ArgumentCommandNode<T, *>): ArgumentType<*>? {
        return command.type
    }

    protected fun execution(command: CommandNode<T>?): Command<R> {
        return NONE as Command<R>
    }

    protected open fun requirement(command: CommandNode<T>?): Predicate<R> {
        return TRUE as Predicate<R>
    }

    protected open fun suggestions(command: ArgumentCommandNode<T, *>): SuggestionProvider<R>? {
        return null
    }

    companion object {
        val NONE: Command<*> = Command { context: CommandContext<Any?>? -> 0 }

        @JvmField
        val TRUE: Predicate<*> = Predicate { source: Any? -> true }
        val EMPTY: SuggestionProvider<*> = SuggestionProvider { suggestions: CommandContext<Any?>?, builder: SuggestionsBuilder -> builder.buildFuture() }
    }
}