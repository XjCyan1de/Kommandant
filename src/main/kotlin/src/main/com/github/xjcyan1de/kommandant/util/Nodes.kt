package src.main.com.github.xjcyan1de.kommandant.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import src.main.com.github.xjcyan1de.kommandant.Aliasable
import src.main.com.github.xjcyan1de.kommandant.Execution
import src.main.com.github.xjcyan1de.kommandant.Mutable
import src.main.com.github.xjcyan1de.kommandant.OptionalContext
import src.main.com.github.xjcyan1de.kommandant.node.Argument
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
            for (alias in (current as Aliasable<T>).aliases()) {
                node.removeChild(alias.name)
            }
        }
        addition.accept(child)
        if (child is Aliasable<*>) {
            val aliases = (child as Aliasable<T>).aliases()
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

    abstract class Builder<T, B : Builder<T, B>> : ArgumentBuilder<T, B>() {
        fun executes(block: OptionalContext<T>.(T) -> Unit): B =
                executes { source, context ->
                    block(context, source)
                }

        fun executes(command: Execution<T>): B = executes(command as Command<T>)

        fun optionally(builder: ArgumentBuilder<T, *>): B = optionally(builder.build())

        fun optionally(node: CommandNode<T>): B {
            then(node)
            for (child in node.children) {
                then(child)
            }
            return getThis()
        }

        fun word(name: String, builder: Argument.Builder<T, String>.() -> Unit): B {
            then(Argument(name, StringArgumentType.word(), builder).build())
            return getThis()
        }

        fun string(name: String, builder: Argument.Builder<T, String>.() -> Unit): B {
            then(Argument(name, StringArgumentType.string(), builder).build())
            return getThis()
        }

        fun greedyString(name: String, builder: Argument.Builder<T, String>.() -> Unit): B {
            then(Argument(name, StringArgumentType.greedyString(), builder).build())
            return getThis()
        }

        fun int(name: String, min: Int=Int.MIN_VALUE, max: Int = Int.MAX_VALUE, builder: Argument.Builder<T, Int>.() -> Unit): B {
            then(Argument(name, IntegerArgumentType.integer(min, max), builder).build())
            return getThis()
        }

        fun long(name: String, min: Long=Long.MIN_VALUE, max: Long = Long.MAX_VALUE, builder: Argument.Builder<T, Long>.() -> Unit): B {
            then(Argument(name, LongArgumentType.longArg(min, max), builder).build())
            return getThis()
        }

        fun float(name: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE, builder: Argument.Builder<T, Float>.() -> Unit): B {
            then(Argument(name, FloatArgumentType.floatArg(min, max), builder).build())
            return getThis()
        }

        fun double(name: String, min: Double=Double.MIN_VALUE, max: Double = Double.MAX_VALUE, builder: Argument.Builder<T, Double>.() -> Unit): B {
            then(Argument(name, DoubleArgumentType.doubleArg(min, max), builder).build())
            return getThis()
        }

        fun boolean(name: String,  builder: Argument.Builder<T, Boolean>.() -> Unit): B {
            then(Argument(name, BoolArgumentType.bool(), builder).build())
            return getThis()
        }
    }
}