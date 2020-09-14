package src.main.com.github.xjcyan1de.kommandant

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.CommandNode

interface Mutable<T> {
    fun addChild(child: CommandNode<T>)
    fun removeChild(child: String): CommandNode<T>?

    fun getCommand(): Command<T>
    fun setCommand(command: Command<T>)

    fun getRedirect(): CommandNode<T>?
    fun setRedirect(destination: CommandNode<T>?)
}