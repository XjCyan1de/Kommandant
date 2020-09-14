package src.main.com.github.xjcyan1de.kommandant.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import src.main.com.github.xjcyan1de.kommandant.Aliasable
import src.main.com.github.xjcyan1de.kommandant.Execution
import src.main.com.github.xjcyan1de.kommandant.Mutable
import java.util.function.Consumer

object Nodes {
    @JvmStatic
    fun <Node, T> addChild(node: Node, child: CommandNode<T>, addition: Consumer<CommandNode<T>>) where Node : CommandNode<T>?, Node : Mutable<T>? {
        val current = node!!.getChild(child.name)
        if (current != null) {
            node.removeChild(current.name) // Needs to be removed otherwise child will not replace current
            for (grandchild in current.children) {
                child.addChild(grandchild) // Grandchildren need to be added since the current child was removed
            }
        }
        if (current is Aliasable<*>) {
            for (alias in (current as Aliasable<T>).aliases()!!) {
                node.removeChild(alias.name)
            }
        }
        addition.accept(child)
        if (child is Aliasable<*>) {
            val aliases = (child as Aliasable<T>).aliases()
            for (alias in aliases!!) {
                addition.accept(alias)
            }
            if (current != null) {
                for (alias in aliases) {
                    for (grandchild in current.children) {
                        alias!!.addChild(grandchild)
                    }
                }
            }
        }
    }

    abstract class Builder<T, B : Builder<T, B>> : ArgumentBuilder<T, B>() {
        fun executes(command: Execution<T>?): B {
            return executes(command as Command<T>?)
        }

        fun optionally(builder: ArgumentBuilder<T, *>): B {
            return optionally(builder.build())
        }

        fun optionally(node: CommandNode<T>): B {
            then(node)
            for (child in node.children) {
                then(child)
            }
            return getThis()
        }
    }
}