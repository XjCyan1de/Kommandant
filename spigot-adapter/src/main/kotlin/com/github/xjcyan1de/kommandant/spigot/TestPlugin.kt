package com.github.xjcyan1de.kommandant.spigot

import src.main.com.github.xjcyan1de.kommandant.dispatcher.Dispatcher
import src.main.com.github.xjcyan1de.kommandant.node.Argument
import src.main.com.github.xjcyan1de.kommandant.node.Literal
import com.mojang.brigadier.arguments.StringArgumentType
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TestPlugin : JavaPlugin() {
    override fun onEnable() {
        Dispatcher.of(this).apply {
            register(
                    Literal.of("gm").executes { source, context ->
                        source.sendMessage("/gm mode")
                    }.then(Argument.of("mode", StringArgumentType.word())
                            .suggests { commandContext, suggestionsBuilder ->
                                suggestionsBuilder.suggest("survival").suggest("creative").suggest("adventure").buildFuture()
                            }
                            .requires {
                                it.isOp
                            }
                            .executes { source, context ->
                                val rawMode = context.getArgument("mode", String::class.java)
                                val gameMode = GameMode.valueOf(rawMode.toUpperCase())
                                (source as? Player)?.gameMode = gameMode
                                source.sendMessage(ChatColor.GREEN.toString()+"Change to "+gameMode)
                            }
                    )
            )
            update()
        }
    }
}