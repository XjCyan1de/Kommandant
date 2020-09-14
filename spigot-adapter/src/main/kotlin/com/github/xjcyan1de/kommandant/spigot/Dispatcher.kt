package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.node.Literal
import com.github.xjcyan1de.kommandant.node.Root
import com.github.xjcyan1de.kommandant.tree.TreeWalker
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.plugin.Plugin

class Dispatcher(
        val server: Server,
        val root: Root<SpigotCommand, CommandSender>
) : CommandDispatcher<CommandSender>(), Listener {
    val walker = TreeWalker(SpigotMapper(this))
    var vanillaDispatcher: CommandDispatcher<Any> = getVanillaDispatcher(server)

    fun register(command: Literal.Builder<CommandSender>): LiteralCommandNode<CommandSender> {
        val literal = command.build()
        root.addChild(literal)
        return literal
    }

    fun update() {
        walker.prune(vanillaDispatcher.root, root.children)
        server.onlinePlayers.forEach { player ->
            player.updateCommands()
        }
    }

    @EventHandler
    fun update(event: ServerLoadEvent) {
        vanillaDispatcher = getVanillaDispatcher(server)
        update()
    }

    companion object {
        private val CRAFT_SERVER__CLASS = obc<Any>("CraftServer")
        private val CraftServer_Console__FIELD = CRAFT_SERVER__CLASS.getDeclaredField("console").apply {
            isAccessible = true
        }
        private val MinecraftServer__CLASS = nms<Any>("MinecraftServer")
        private val GET_COMMAND_DISPATCHER__METHOD = MinecraftServer__CLASS.getDeclaredMethod("getCommandDispatcher").apply {
            isAccessible = true
        }
        private val COMMAND_DISPATCHER__CLASS = nms<Any>("CommandDispatcher")
        private val A__METHOD = COMMAND_DISPATCHER__CLASS.getDeclaredMethod("a").apply {
            isAccessible = true
        }

        @Suppress("UNCHECKED_CAST")
        private fun getVanillaDispatcher(server: Server): CommandDispatcher<Any> {
            val console = CraftServer_Console__FIELD.get(server)
            val commandDispatcher = GET_COMMAND_DISPATCHER__METHOD.invoke(console)
            return A__METHOD.invoke(commandDispatcher) as CommandDispatcher<Any>
        }

        @JvmOverloads
        @JvmStatic
        fun of(plugin: Plugin, prefix: String = plugin.name.toLowerCase()): Dispatcher {
            val commandMap = SpigotCommandMap(plugin, prefix)
            val root = Root(prefix, commandMap)
            val dispatcher = Dispatcher(plugin.server, root)
            commandMap.dispatcher = dispatcher
            plugin.server.pluginManager.registerEvents(dispatcher, plugin)
            return dispatcher
        }
    }
}