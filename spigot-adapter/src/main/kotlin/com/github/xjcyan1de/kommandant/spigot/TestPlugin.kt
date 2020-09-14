package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.node.Literal
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TestPlugin : JavaPlugin() {
    override fun onEnable() {
        Dispatcher.of(this).apply {

            register(Literal("gm") {
                val gameModes = mapOf(
                        "survival" to GameMode.SURVIVAL,
                        "creative" to GameMode.CREATIVE,
                        "adventure" to GameMode.ADVENTURE,
                        "spectator" to GameMode.SPECTATOR,
                        "s" to GameMode.SURVIVAL,
                        "c" to GameMode.CREATIVE,
                        "a" to GameMode.ADVENTURE,
                        "sp" to GameMode.SPECTATOR
                )

                word("name") {
                    requires {
                        it is Player && it.hasPermission("command.gamemode")
                    }

                    suggests {
                        suggest("survival")
                        suggest("creative")
                        suggest("adventure")
                        suggest("spectator")
                    }

                    executes {
                        it as Player
                        it.gameMode = gameModes[getArgument("name")!!] ?: throw CommandSyntaxException(SimpleCommandExceptionType { "1" }) { "2" }
                    }
                }

                executes {
                    it.sendMessage("/gm")
                }
            })

            update()
        }
    }
}