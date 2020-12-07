package com.github.xjcyan1de.kommandant.util

import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.server.v1_16_R3.*
import org.bukkit.command.*
import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.bukkit.craftbukkit.v1_16_R3.command.CraftBlockCommandSender
import org.bukkit.craftbukkit.v1_16_R3.command.ProxiedNativeCommandSender
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftMinecartCommand
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.CommandMinecart
import kotlin.math.max
import kotlin.math.min

internal object Exceptions {
    private val EMPTY = arrayOfNulls<Any>(0)

    // Source: net.minecraft.server.CommandDispatcher #line: 188
    @JvmStatic
    fun report(sender: CommandSender, exception: CommandSyntaxException) {
        val listener = from(sender)
        listener.sendFailureMessage(ChatComponentUtils.a(exception.rawMessage))
        val input = exception.input
        if (input != null && exception.cursor >= 0) {
            val index = min(input.length, exception.cursor)
            val text = ChatComponentText("").a(EnumChatFormat.GRAY).format { modifier: ChatModifier -> modifier.setChatClickable(ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, input)) }
            if (index > 10) {
                text.c("...")
            }
            text.c(input.substring(max(0, index - 10), index))
            if (index < input.length) {
                val error = ChatComponentText(input.substring(index)).a(EnumChatFormat.RED, EnumChatFormat.UNDERLINE)
                text.addSibling(error)
            }
            val context = ChatMessage("command.context.here").a(EnumChatFormat.RED, EnumChatFormat.ITALIC)
            text.addSibling(context)
            listener.sendFailureMessage(text)
        }
    }

    // Source: net.minecraft.server.CommandDispatcher #line: 213
    @JvmStatic
    fun report(sender: CommandSender, exception: Exception) {
        val listener = from(sender)
        val message = exception.message
        val details = ChatComponentText(message ?: exception.javaClass.name)

        // We send the stacktrace regardless of whether debug is enabled since we
        // cannot access the CommandDispatcher's logger.
        val stacktrace = exception.stackTrace
        for (i in 0 until Math.min(stacktrace.size, 3)) {
            val element = stacktrace[i]
            details.c("\n\n").c(element.methodName).c("\n ").c(element.fileName).c(":").c(element.lineNumber.toString())
        }
        val failure = ChatMessage("command.failed").format { modifier: ChatModifier -> modifier.setChatHoverable(ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, details)) }
        listener.sendFailureMessage(failure)
        if (SharedConstants.d) {
            listener.sendFailureMessage(ChatComponentText(SystemUtils.d(exception)))
            // We do not log the error since we cannot access the logger
        }
    }

    // Source: package org.bukkit.craftbukkit.command.VanillaCommandWrapper#getListener(CommandSender)
    fun from(sender: CommandSender): CommandListenerWrapper = when (sender) {
        is Player -> (sender as CraftPlayer).handle.commandListener
        is BlockCommandSender -> (sender as CraftBlockCommandSender).wrapper
        is CommandMinecart -> (sender as CraftMinecartCommand).handle.commandBlock.wrapper
        is RemoteConsoleCommandSender -> (MinecraftServer.getServer() as DedicatedServer).remoteControlCommandListener.wrapper
        is ConsoleCommandSender -> (sender.getServer() as CraftServer).server.serverCommandListener
        is ProxiedCommandSender -> (sender as ProxiedNativeCommandSender).handle
        else -> throw IllegalArgumentException("Cannot make $sender a vanilla command listener")
    }
}