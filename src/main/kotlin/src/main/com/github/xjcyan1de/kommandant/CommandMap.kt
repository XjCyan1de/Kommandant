
package src.main.com.github.xjcyan1de.kommandant

import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender
import src.main.com.github.xjcyan1de.kommandant.dispatcher.DispatcherCommand

interface CommandMap {
    fun register(command: LiteralCommandNode<CommandSender>): DispatcherCommand?
    fun unregister(name: String?): DispatcherCommand?
}