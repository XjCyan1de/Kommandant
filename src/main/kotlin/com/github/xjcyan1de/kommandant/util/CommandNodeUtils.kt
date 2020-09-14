@file:Suppress("UNCHECKED_CAST")

package com.github.xjcyan1de.kommandant.util

import com.github.xjcyan1de.kommandant.Aliasable
import com.github.xjcyan1de.kommandant.Mutable
import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.CommandNode
import java.util.function.Consumer


private val COMMAND_FIELD = CommandNode::class.java.getDeclaredField("command").apply { isAccessible = true }
private val CHILDREN_FIELD = CommandNode::class.java.getDeclaredField("children").apply { isAccessible = true }
private val LITERALS_FIELD = CommandNode::class.java.getDeclaredField("literals").apply { isAccessible = true }
private val ARGUMENTS_FIELD = CommandNode::class.java.getDeclaredField("arguments").apply { isAccessible = true }

fun <T> CommandNode<T>.execution(execution: Command<T>) = apply {
    COMMAND_FIELD.set(this, execution)
}

fun <T> CommandNode<T>.remove(child: String): CommandNode<T>? {
    val children = CHILDREN_FIELD.get(this) as MutableMap<String, CommandNode<T>>
    val literals = LITERALS_FIELD.get(this) as MutableMap<String, *>
    val arguments = ARGUMENTS_FIELD.get(this) as MutableMap<String, *>

    val removed = children.remove(child)
    literals.remove(child)
    arguments.remove(child)

    if (removed is Aliasable<*>) {
        removed.aliases.forEach { alias ->
            val name = alias.name
            children.remove(name)
            literals.remove(name)
            arguments.remove(name)
        }
    }

    return removed
}

fun <Node, T> Node.add(child: CommandNode<T>, addition: Consumer<CommandNode<T>>) where Node : CommandNode<T>, Node : Mutable<T> {
    val current = this.getChild(child.name)
    if (current != null) {
        this.removeChild(current.name) // Needs to be removed otherwise child will not replace current
        for (grandchild in current.children) {
            child.addChild(grandchild) // Grandchildren need to be added since the current child was removed
        }
    }
    if (current is Aliasable<*>) {
        for (alias in (current as Aliasable<T>).aliases) {
            this.removeChild(alias.name)
        }
    }
    addition.accept(child)
    if (child is Aliasable<*>) {
        val aliases = (child as Aliasable<T>).aliases
        for (alias in aliases) {
            addition.accept(alias)
        }
        if (current != null) {
            for (alias in aliases) {
                for (grandchild in current.children) {
                    alias.addChild(grandchild)
                }
            }
        }
    }
}