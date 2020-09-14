
package src.main.com.github.xjcyan1de.kommandant.spigot

import src.main.com.github.xjcyan1de.kommandant.dispatcher.DispatcherCommand
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender

interface CommandMap {
    fun register(command: LiteralCommandNode<CommandSender>): DispatcherCommand?
    fun unregister(name: String?): DispatcherCommand?
}