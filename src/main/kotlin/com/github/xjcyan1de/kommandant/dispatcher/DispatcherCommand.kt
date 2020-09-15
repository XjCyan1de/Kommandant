package com.github.xjcyan1de.kommandant.dispatcher

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin
import com.github.xjcyan1de.kommandant.util.Exceptions.report

class DispatcherCommand(
        name: String,
        private var plugin: Plugin,
        var dispatcher: CommandDispatcher<CommandSender>,
        usage: String,
        aliases: List<String>
) : Command(name, "", usage, aliases), PluginIdentifiableCommand {
    override fun execute(sender: CommandSender, label: String, vararg arguments: String): Boolean {
        if (!testPermission(sender)) {
            return true
        }
        val reader = StringReader(join(label, arguments))
        if (reader.canRead() && reader.peek() == '/') {
            reader.skip()
        }
        try {
            dispatcher.execute(reader, sender)
        } catch (e: CommandSyntaxException) {
            report(sender, e)
        } catch (e: Exception) {
            report(sender, e)
        }
        return true
    }

    private fun join(name: String, arguments: Array<out String>): String {
        var command = "/$name"
        if (arguments.isNotEmpty()) {
            command += " " + java.lang.String.join(" ", *arguments)
        }
        return command
    }

    override fun getPlugin(): Plugin {
        return plugin
    }
}