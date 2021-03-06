package com.github.xjcyan1de.kommandant.util

import com.github.xjcyan1de.kommandant.Mutable
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.RootCommandNode
import java.util.*
import java.util.function.Predicate

class TreeWalker<T, R>(private val mapper: Mapper<T, R>) {
    protected val mappings: MutableMap<CommandNode<T>, CommandNode<R>?>
    fun prune(root: RootCommandNode<R>, commands: Collection<CommandNode<T>>) {
        for (child in commands) {
            Commands.remove(root, child.name)
            val result = map(child, null)
            if (result != null) {
                root.addChild(result)
            }
        }
        mappings.clear()
    }

    fun add(root: RootCommandNode<R>, commands: Collection<CommandNode<T>>, source: T, requirement: Predicate<CommandNode<T>?>) {
        for (command in commands) {
            if (requirement.test(command)) {
                val result = map(command, source)
                if (result != null) {
                    root.addChild(result)
                }
            }
        }
        mappings.clear()
    }

    protected fun map(command: CommandNode<T>, source: T?): CommandNode<R>? {
        if (source != null && command.requirement != null && !command.canUse(source)) {
            return null
        }
        var result = mappings[command]
        if (result == null) {
            result = mapper.map(command)
            mappings[command] = result
            redirect(command.redirect, result, source)
            descend(command.children, result, source)
        }
        return result
    }

    protected fun redirect(destination: CommandNode<T>?, result: CommandNode<R>?, source: T?) {
        if (destination != null && result is Mutable<*>) {
            (result as Mutable<R>).setRedirect(map(destination, source))
        }
    }

    protected fun descend(children: Collection<CommandNode<T>>, command: CommandNode<R>?, source: T?) {
        for (child in children) {
            val result = map(child, source)
            if (result != null) {
                command!!.addChild(result)
            }
        }
    }

    init {
        mappings = IdentityHashMap()
    }
}