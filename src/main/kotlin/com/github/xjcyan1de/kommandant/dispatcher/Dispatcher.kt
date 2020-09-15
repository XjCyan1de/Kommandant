package com.github.xjcyan1de.kommandant.dispatcher

import com.github.xjcyan1de.kommandant.node.Literal
import com.github.xjcyan1de.kommandant.node.Root
import com.github.xjcyan1de.kommandant.util.TreeWalker
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.RootCommandNode
import net.minecraft.server.v1_16_R2.CommandListenerWrapper
import net.minecraft.server.v1_16_R2.MinecraftServer
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_16_R2.CraftServer
import org.bukkit.craftbukkit.v1_16_R2.command.CraftCommandMap
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.plugin.Plugin

class Dispatcher private constructor(
        server: Server,
        private val root: Root
) : CommandDispatcher<CommandSender>(root), Listener {
    var dispatcher: CommandDispatcher<CommandListenerWrapper>
    var walker: TreeWalker<CommandSender, CommandListenerWrapper>
    private val server: MinecraftServer

    fun register(commands: Map<String?, CommandNode<CommandSender?>?>) {
        for (command in commands.values) {
            getRoot().addChild(command)
        }
    }

    fun register(name: String, vararg aliases: String, builder: Literal.Builder<CommandSender>.()->Unit) = register(Literal(name, aliases.asIterable(), builder))

    fun register(command: Literal.Builder<CommandSender>): Literal<CommandSender> {
        val literal = command.build()
        getRoot().addChild(literal)
        return literal
    }

    fun update() {
        walker.prune(dispatcher.root, root.children)
        for (player in server.server.onlinePlayers) {
            player.updateCommands()
        }
    }

    @EventHandler
    protected fun update(event: ServerLoadEvent?) {
        dispatcher = server.commandDispatcher.a()
        update()
    }

    override fun getRoot(): RootCommandNode<CommandSender> = root

    companion object {
        fun of(plugin: Plugin): Dispatcher {
            val prefix = plugin.name.toLowerCase()
            val server = plugin.server as CraftServer
            val map = SpigotMap(prefix, plugin, (server.commandMap as CraftCommandMap))
            val root = Root(prefix, map)
            val dispatcher = Dispatcher(server, root)
            map.dispatcher = dispatcher
            server.pluginManager.registerEvents(dispatcher, plugin)
            return dispatcher
        }
    }

    init {
        this.server = (server as CraftServer).server
        dispatcher = this.server.getCommandDispatcher().a()
        walker = TreeWalker(SpigotMapper(this))
    }
}