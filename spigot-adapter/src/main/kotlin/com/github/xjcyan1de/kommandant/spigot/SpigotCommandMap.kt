package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.CommandMap
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class SpigotCommandMap(
        val plugin: Plugin,
        val prefix: String = plugin.name.toLowerCase(),
) : CommandMap<SpigotCommand, CommandSender> {
    lateinit var dispatcher: SpigotDispatcher

    override fun register(command: LiteralCommandNode<CommandSender>): SpigotCommand {
        TODO("Not yet implemented")
    }

    override fun unregister(name: String): SpigotCommand {
        TODO("Not yet implemented")
    }
}