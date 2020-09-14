package src.main.com.github.xjcyan1de.kommandant.util

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.CommandNode
import src.main.com.github.xjcyan1de.kommandant.Aliasable
import java.lang.invoke.MethodHandles

object Commands {
    val type = MethodHandles.privateLookupIn(CommandNode::class.java, MethodHandles.lookup())
    val COMMAND = type.findVarHandle(CommandNode::class.java, "command", Command::class.java)
    val CHILDREN = type.findVarHandle(CommandNode::class.java, "children", MutableMap::class.java)
    val LITERALS = type.findVarHandle(CommandNode::class.java, "literals", MutableMap::class.java)
    val ARGUMENTS = type.findVarHandle(CommandNode::class.java, "arguments", MutableMap::class.java)

    @JvmStatic
    fun <T> execution(command: CommandNode<T>, execution: Command<T>) {
        COMMAND.set(command, execution)
    }

    @JvmStatic
    fun <T> remove(command: CommandNode<T>?, child: String): CommandNode<T>? {
        val children = CHILDREN.get(command) as MutableMap<String, CommandNode<T>>
        val literals = LITERALS.get(command) as MutableMap<String, *>
        val arguments = ARGUMENTS.get(command) as MutableMap<String, *>
        val removed = children.remove(child)
        literals.remove(child)
        arguments.remove(child)
        if (removed is Aliasable<*>) {
            for (alias in (removed as Aliasable<*>).aliases()) {
                val name = alias.name
                children.remove(name)
                literals.remove(name)
                arguments.remove(name)
            }
        }
        return removed
    }
}