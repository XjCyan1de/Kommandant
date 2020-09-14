package src.main.com.github.xjcyan1de.kommandant.node

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender
import src.main.com.github.xjcyan1de.kommandant.Aliasable
import src.main.com.github.xjcyan1de.kommandant.Execution
import src.main.com.github.xjcyan1de.kommandant.Mutable
import src.main.com.github.xjcyan1de.kommandant.util.Commands.execution
import src.main.com.github.xjcyan1de.kommandant.util.Commands.remove
import src.main.com.github.xjcyan1de.kommandant.util.Nodes
import src.main.com.github.xjcyan1de.kommandant.util.Nodes.addChild
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.collections.ArrayList

class Literal<T>(
        name: String,
        val aliases: MutableList<LiteralCommandNode<T>>,
        alias: Boolean, command: Command<T>,
        requirement: Predicate<T>,
        private var destination: CommandNode<T>?,
        modifier: RedirectModifier<T>?,
        fork: Boolean
) : LiteralCommandNode<T>(name, command, requirement, destination, modifier, fork), Aliasable<T>, Mutable<T> {
    private val addition: Consumer<CommandNode<T>>
    override val isAlias: Boolean

    constructor(name: String, execution: Execution<T>, requirement: Predicate<T>) : this(name, execution, requirement, null, null, false) {}

    @JvmOverloads
    constructor(name: String, command: Command<T>, requirement: Predicate<T>, destination: CommandNode<T>? = null , modifier: RedirectModifier<T>? = null, fork: Boolean = false) : this(name, ArrayList<LiteralCommandNode<T>>(0), false, command, requirement, destination, modifier, fork) {
    }

    override fun addChild(child: CommandNode<T>) {
        addChild(this, child, addition)
        for (alias in aliases) {
            alias.addChild(child)
        }
    }

    override fun removeChild(child: String): CommandNode<T>? {
        val removed = remove(this, child)
        for (alias in aliases) {
            remove(alias, child)
        }
        return removed
    }

    override fun aliases(): MutableList<LiteralCommandNode<T>> {
        return aliases
    }

    override fun setCommand(command: Command<T>) {
        execution(this, command)
        for (alias in aliases) {
            execution(alias, command)
        }
    }

    override fun getRedirect(): CommandNode<T>? = destination

    override fun setRedirect(destination: CommandNode<T>?) {
        this.destination = destination
        for (alias in aliases) {
            if (alias is Mutable<*>) {
                (alias as Mutable<T>).setRedirect(destination)
            }
        }
    }

    class Builder<T>(var name: String) : Nodes.Builder<T, Builder<T>>() {
        var aliases: MutableList<String> = ArrayList(0)

        fun alias(vararg aliases: String): Builder<T> {
            Collections.addAll(this.aliases, *aliases)
            return this
        }

        override fun build(): Literal<T> {
            val literal = Literal(name, command, requirement, redirect, redirectModifier, isFork)
            for (child in arguments) {
                literal.addChild(child)
            }
            for (alias in aliases) {
                alias(literal, alias)
            }
            return literal
        }

        override fun getThis(): Builder<T> {
            return this
        }
    }

    companion object {
        fun <T> alias(command: LiteralCommandNode<T>, alias: String): Literal<T> {
            val literal = Literal(alias, ArrayList(0), true, command.command, command.requirement, command.redirect, command.redirectModifier, command.isFork)
            for (child in command.children) {
                literal.addChild(child!!)
            }
            if (command is Aliasable<*>) {
                (command as Aliasable<T>).aliases().add(literal)
            }
            return literal
        }

        fun <T> builder(name: String): Builder<T> {
            return Builder(name)
        }

        fun of(name: String): Builder<CommandSender> {
            return Builder(name)
        }
    }

    init {
        addition = Consumer { node: CommandNode<T>? -> super.addChild(node) }
        isAlias = alias
    }
}