package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.node.Root
import com.mojang.brigadier.CommandDispatcher
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.plugin.Plugin

class SpigotDispatcher(
        val server: Server,
        val root: Root<SpigotCommand, CommandSender>
) : Listener {
    var vanillaDispatcher: CommandDispatcher<*> = getVanillaDispatcher(server)

    @EventHandler
    fun update(event: ServerLoadEvent) {
        TODO()
    }

    companion object {
        val CRAFT_SERVER__CLASS = obc<Any>("CraftServer")
        val GET_COMMAND_DISPATCHER__METHOD = CRAFT_SERVER__CLASS.getDeclaredMethod("getCommandDispatcher").apply {
            isAccessible = true
        }
        val COMMAND_DISPATCHER__CLASS = nms<Any>("CommandDispatcher")
        val A__METHOD = COMMAND_DISPATCHER__CLASS.getDeclaredMethod("a").apply {
            isAccessible = true
        }

        @Suppress("UNCHECKED_CAST")
        fun getVanillaDispatcher(server: Server): CommandDispatcher<*> {
            val commandDispatcher = GET_COMMAND_DISPATCHER__METHOD.invoke(server)
            return A__METHOD.invoke(commandDispatcher) as CommandDispatcher<*>
        }

        @JvmOverloads
        @JvmStatic
        fun of(plugin: Plugin, prefix: String = plugin.name.toLowerCase()): SpigotDispatcher {
            val commandMap = SpigotCommandMap(plugin, prefix)
            val root = Root(prefix, commandMap)
            val dispatcher = SpigotDispatcher(plugin.server, root)
            commandMap.dispatcher = dispatcher
            plugin.server.pluginManager.registerEvents(dispatcher, plugin)
            return dispatcher
        }
    }
}