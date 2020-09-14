package src.main.com.github.xjcyan1de.kommandant.node

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import org.bukkit.command.CommandSender
import src.main.com.github.xjcyan1de.kommandant.Execution
import src.main.com.github.xjcyan1de.kommandant.Mutable
import src.main.com.github.xjcyan1de.kommandant.util.Commands.execution
import src.main.com.github.xjcyan1de.kommandant.util.Commands.remove
import src.main.com.github.xjcyan1de.kommandant.util.Nodes
import src.main.com.github.xjcyan1de.kommandant.util.Nodes.addChild
import java.util.function.Consumer
import java.util.function.Predicate

class Argument<T, V>(name: String?, type: ArgumentType<V>?, command: Command<T>?, requirement: Predicate<T>?, private var destination: CommandNode<T>?, modifier: RedirectModifier<T>?, fork: Boolean, suggestions: SuggestionProvider<T>?) : ArgumentCommandNode<T, V>(name, type, command, requirement, destination, modifier, fork, suggestions), Mutable<T> {
    private val addition = Consumer { node: CommandNode<T> -> super.addChild(node) }

    constructor(name: String?, type: ArgumentType<V>?, command: Command<T>?, requirement: Predicate<T>?, suggestions: SuggestionProvider<T>?) : this(name, type, command, requirement, null, null, false, suggestions)
    constructor(name: String?, type: ArgumentType<V>?, execution: Execution<T>?, requirement: Predicate<T>?, suggestions: SuggestionProvider<T>?) : this(name, type, execution, requirement, null, null, false, suggestions)

    override fun addChild(child: CommandNode<T>) {
        addChild(this, child, addition)
    }

    override fun removeChild(child: String): CommandNode<T>? {
        return remove(this, child)
    }

    override fun setCommand(command: Command<T>) {
        execution(this, command)
    }

    override fun getRedirect(): CommandNode<T>? {
        return destination
    }

    override fun setRedirect(destination: CommandNode<T>?) {
        this.destination = destination
    }

    class Builder<T, V>(var name: String, var type: ArgumentType<V>) : Nodes.Builder<T, Builder<T, V>>() {
        var suggestions: SuggestionProvider<T>? = null
        fun suggests(suggestions: SuggestionProvider<T>?): Builder<T, V> {
            this.suggestions = suggestions
            return getThis()
        }

        override fun getThis(): Builder<T, V> {
            return this
        }

        override fun build(): Argument<T, V> {
            val parameter = Argument(name, type, command, requirement, redirect, redirectModifier, isFork, suggestions)
            for (child in arguments) {
                parameter.addChild(child)
            }
            return parameter
        }
    }

    companion object {
        fun <T, V> builder(name: String, type: ArgumentType<V>): Builder<T, V> {
            return Builder(name, type)
        }

        fun <V> of(name: String, type: ArgumentType<V>): Builder<CommandSender, V> {
            return Builder(name, type)
        }
    }
}