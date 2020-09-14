@file:Suppress("UNCHECKED_CAST")

package com.github.xjcyan1de.kommandant.util

import com.github.xjcyan1de.kommandant.Execution
import com.github.xjcyan1de.kommandant.node.Aliasable
import com.mojang.brigadier.tree.CommandNode

private val COMMAND_FIELD = CommandNode::class.java.getDeclaredField("command").apply { isAccessible = true }
private val CHILDREN_FIELD = CommandNode::class.java.getDeclaredField("children").apply { isAccessible = true }
private val LITERALS_FIELD = CommandNode::class.java.getDeclaredField("literals").apply { isAccessible = true }
private val ARGUMENTS_FIELD = CommandNode::class.java.getDeclaredField("arguments").apply { isAccessible = true }

fun <T> CommandNode<T>.execution(execution: Execution<T>) = apply {
    COMMAND_FIELD.set(this, execution)
}

fun <T> CommandNode<T>.remove(child: String): CommandNode<T>? {
    val children = CHILDREN_FIELD.get(this) as MutableMap<String, CommandNode<T>>
    val literals = LITERALS_FIELD.get(this) as MutableMap<String,*>
    val arguments = ARGUMENTS_FIELD.get(this) as MutableMap<String,*>

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