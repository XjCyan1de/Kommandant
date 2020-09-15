package com.github.xjcyan1de.kommandant.dispatcher

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_16_R2.command.CraftCommandMap
import org.bukkit.plugin.Plugin
import com.github.xjcyan1de.kommandant.Aliasable
import com.github.xjcyan1de.kommandant.CommandMap
import java.util.*

internal class SpigotMap(var prefix: String, var plugin: Plugin, var map: CraftCommandMap) : CommandMap {
    lateinit var dispatcher: CommandDispatcher<CommandSender>
    override fun register(command: LiteralCommandNode<CommandSender>): DispatcherCommand? {
        // We don't need to check if map contains "prefix:command_name" since Spigot will
        // always override it
        if (map.knownCommands.containsKey(command.name)) {
            return null
        }
        val wrapped = wrap(command)
        map.register(prefix, wrapped)
        return wrapped
    }

    override fun unregister(name: String?): DispatcherCommand? {
        val commands = map.knownCommands
        val command = commands[name] as? DispatcherCommand
                ?: return null
        commands.remove(name, command)
        commands.remove("$prefix:$name", command)
        for (alias in command.aliases) {
            commands.remove(alias, command)
            commands.remove("$prefix:$alias", command)
        }
        command.unregister(map)
        return command
    }

    fun wrap(command: LiteralCommandNode<CommandSender>): DispatcherCommand {
        val aliases = ArrayList<String>()
        if (command is Aliasable<*>) {
            for (alias in (command as Aliasable<*>).aliases()) {
                aliases.add(alias.name)
            }
        }
        return DispatcherCommand(command.name, plugin, dispatcher, command.usageText, aliases)
    }
}