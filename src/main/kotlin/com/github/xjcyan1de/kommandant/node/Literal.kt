package com.github.xjcyan1de.kommandant.node

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import java.util.function.Predicate

class Literal<T>(
        name: String,
        override val aliases: MutableList<LiteralCommandNode<T>> = ArrayList(0),
        override val isAlias: Boolean = false,
        command: Command<T>,
        requirement: Predicate<T>,
        redirect: CommandNode<T>? = null,
        modifier: RedirectModifier<T>? = null,
        forks: Boolean = false
) : LiteralCommandNode<T>(
        name, command, requirement, redirect, modifier, forks
), Aliasable<T> {
    class Builder<T> constructor(
            var name: String? = null,
            var aliases: MutableCollection<String>? = null
    ) : NodeBuilder<T, Builder<T>>() {
        fun name(name: String): Builder<T> = apply {
            this.name = name
        }

        fun alias(vararg aliases: String): Builder<T> = apply {
            if (this.aliases == null) this.aliases = aliases.toMutableList()
            else this.aliases?.addAll(aliases)
        }

        fun alias(aliases: Iterable<String>): Builder<T> = apply {
            if (this.aliases == null) this.aliases = aliases.toMutableList()
            else this.aliases?.addAll(aliases)
        }

        fun alias(aliases: Sequence<String>): Builder<T> = apply {
            if (this.aliases == null) this.aliases = aliases.toMutableList()
            else this.aliases?.addAll(aliases)
        }

        override fun getThis(): Builder<T> = this

        override fun build(): Literal<T> {
            val literal = Literal(requireNotNull(name), ArrayList(0), false, command, requirement, redirect, redirectModifier, isFork)
            arguments.forEach {
                literal.addChild(it)
            }
            aliases?.forEach {
                alias(literal, it)
            }

            return literal
        }
    }

    companion object {
        @JvmStatic
        fun <T> of(name: String): Builder<T> = Builder(name)

        @JvmStatic
        fun <T> alias(command: LiteralCommandNode<T>, alias: String): Literal<T> {
            val literal: Literal<T> = Literal(
                    alias,
                    ArrayList(0),
                    true,
                    command.command,
                    command.requirement,
                    command.redirect,
                    command.redirectModifier,
                    command.isFork)

            command.children.forEach {
                literal.addChild(it)
            }

            @Suppress("UNCHECKED_CAST")
            if (command as? Aliasable<T> != null) {
                command.aliases.add(literal)
            }

            return literal
        }
    }
}

fun <T> Literal(name: String, vararg aliases: String, block: Literal.Builder<T>.() -> Unit = {}) =
        Literal.of<T>(name).alias(*aliases).apply(block).build()