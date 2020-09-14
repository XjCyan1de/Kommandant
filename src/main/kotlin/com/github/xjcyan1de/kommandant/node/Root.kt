package com.github.xjcyan1de.kommandant.node

import com.github.xjcyan1de.kommandant.CommandMap
import com.github.xjcyan1de.kommandant.CommandWrapper
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode

open class Root<C : CommandWrapper, T>(
        val prefix: String,
        val commandMap: CommandMap<C, T>
) : RootCommandNode<T>() {
    @Suppress("UNCHECKED_CAST")
    override fun addChild(command: CommandNode<T>) {
        if (getChild(command.name) != null) {
            throw IllegalArgumentException("Invalid command: '${command.name}', root already contains a child with the same name")
        }
        if (command is LiteralCommandNode<*>) {
            throw IllegalArgumentException("Invalid command: '${command.name}', commands registered to root must be a literal")
        }
        val literal = command as LiteralCommandNode<T>
        val wrapper = commandMap.register(literal)

        super.addChild(Literal.alias(literal, prefix + ":" + literal.name))
        super.addChild(literal)

        if (literal is Aliasable<*>) {
            val aliases = literal.aliases as List<LiteralCommandNode<T>>
            aliases.forEach { alias ->
                if (wrapper.aliases.contains(alias.name)) {
                    super.addChild(Literal.alias(literal, prefix + ":" + alias.name))
                    super.addChild(alias)
                }
            }
        }
    }
}