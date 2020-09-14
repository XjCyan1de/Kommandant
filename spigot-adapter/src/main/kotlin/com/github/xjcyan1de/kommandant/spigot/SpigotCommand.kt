@file:Suppress("INAPPLICABLE_JVM_NAME") // https://youtrack.jetbrains.com/issue/KT-31420

package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.CommandWrapper
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

class SpigotCommand(
        name: String,
        private val plugin: Plugin,
        val dispatcher: CommandDispatcher<CommandSender>,
        usageMessage: String,
        aliases: List<String>
) : Command(name, "", usageMessage, aliases), PluginIdentifiableCommand, CommandWrapper {
    override fun getPlugin(): Plugin = plugin

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        if (!testPermission(sender)) {
            return true
        } else {
            val input = join(label, args)
            val reader = StringReader(input)

            if (reader.canRead() && reader.peek() == '/') {
                reader.skip()
            }

            try {
                dispatcher.execute(reader, sender)
            } catch (e: CommandSyntaxException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return true
        }
    }

    private fun join(name: String, arguments: Array<out String>): String {
        val sb = StringBuilder("/").append(name)
        arguments.forEach {
            sb.append(" ").append(it)
        }
        return sb.toString()
    }
}