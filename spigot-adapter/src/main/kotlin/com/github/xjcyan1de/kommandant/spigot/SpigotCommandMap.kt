package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.Aliasable
import com.github.xjcyan1de.kommandant.CommandMap
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class SpigotCommandMap(
        val plugin: Plugin,
        val prefix: String = plugin.name.toLowerCase(),
) : CommandMap<SpigotCommand, CommandSender> {
    lateinit var dispatcher: CommandDispatcher<CommandSender>

    override fun register(command: LiteralCommandNode<CommandSender>): SpigotCommand? {
        if (plugin.server.commandMap.knownCommands.containsKey(command.name)) return null

        val wrapped = command.asSpigotCommand()
        plugin.server.commandMap.register(prefix, wrapped)
        return wrapped
    }

    override fun unregister(name: String): SpigotCommand? {
        val commands = plugin.server.commandMap.knownCommands
        val command = commands[name]
        if (command !is SpigotCommand) return null

        commands.remove(name, command)
        commands.remove("$prefix:$name", command)

        command.aliases.forEach { alias ->
            commands.remove(alias, command)
            commands.remove("$prefix:$alias", command)
        }

        command.unregister(plugin.server.commandMap)

        return command
    }

    private fun LiteralCommandNode<CommandSender>.asSpigotCommand(): SpigotCommand {
        val aliases = if (this is Aliasable<*>) {
            aliases.map { it.name }
        } else emptyList<String>()

        return SpigotCommand(name, plugin, dispatcher, usageText, aliases)
    }
}