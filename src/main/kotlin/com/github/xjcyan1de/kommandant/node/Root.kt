package com.github.xjcyan1de.kommandant.node

import com.github.xjcyan1de.kommandant.Aliasable
import com.github.xjcyan1de.kommandant.CommandMap
import com.github.xjcyan1de.kommandant.Mutable
import com.github.xjcyan1de.kommandant.util.Commands.remove
import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import org.bukkit.command.CommandSender
import java.util.*

class Root(private val prefix: String, private val map: CommandMap) : RootCommandNode<CommandSender>(), Mutable<CommandSender> {
    override fun addChild(command: CommandNode<CommandSender>) {
        require(getChild(command.name) == null) { "Invalid command: '" + command.name + "', root already contains a child with the same name" }
        require(command is LiteralCommandNode<*>) { "Invalid command: '" + command.name + "', commands registered to root must be a literal" }
        val literal = command as LiteralCommandNode<CommandSender>
        val wrapper = map.register(literal) ?: return
        super.addChild(Literal.alias(literal, prefix + ":" + literal.name))
        if (wrapper.name == wrapper.label) {
            super.addChild(literal)
        }
        if (literal is Aliasable<*>) {
            for (alias in ArrayList((literal as Aliasable<CommandSender>).aliases())) {
                if (wrapper.aliases.contains(alias!!.name)) {
                    super.addChild(Literal.alias(literal, prefix + ":" + alias.name))
                    super.addChild(alias)
                }
            }
        }
    }

    override fun removeChild(child: String): CommandNode<CommandSender>? = remove<CommandSender>(this, child)

    override fun getCommand(): Command<CommandSender> {
        return super.getCommand()
    }

    override fun getRedirect(): CommandNode<CommandSender> {
        return super.getRedirect()
    }

    fun map(): CommandMap = map

    override fun setCommand(command: Command<CommandSender>) {
    }

    override fun setRedirect(destination: CommandNode<CommandSender>?) {
    }
}