package com.github.xjcyan1de.kommandant

import com.mojang.brigadier.tree.LiteralCommandNode

interface CommandMap<C : CommandWrapper, T> {
    fun register(command: LiteralCommandNode<T>) : C?

    fun unregister(name: String): C?
}