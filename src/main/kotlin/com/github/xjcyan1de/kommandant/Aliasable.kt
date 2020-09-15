package com.github.xjcyan1de.kommandant

import com.mojang.brigadier.tree.LiteralCommandNode

interface Aliasable<T> {
    fun aliases(): MutableList<LiteralCommandNode<T>>
    val isAlias: Boolean
}