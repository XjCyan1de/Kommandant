package com.github.xjcyan1de.kommandant

import com.mojang.brigadier.tree.LiteralCommandNode

interface Aliasable<T> {
    val aliases: MutableList<LiteralCommandNode<T>>

    val isAlias: Boolean
}