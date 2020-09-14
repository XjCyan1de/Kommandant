package com.github.xjcyan1de.kommandant.spigot

import com.github.xjcyan1de.kommandant.MappedArgumentType
import com.github.xjcyan1de.kommandant.tree.Mapper
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import org.bukkit.command.CommandSender
import java.util.*
import java.util.function.Predicate


@Suppress("UNCHECKED_CAST")
internal class SpigotMapper(
        private val dispatcher: CommandDispatcher<CommandSender>
) : Mapper<CommandSender, Any>() {
    override fun type(command: ArgumentCommandNode<CommandSender, *>): ArgumentType<*> {
        val type = command.type
        return if (type is MappedArgumentType<*>) type.mapped else type
    }

    override fun requirement(command: CommandNode<CommandSender>): Predicate<Any> {
        val requirement = command.requirement
        return if (requirement == null) TRUE as Predicate<Any>
        else Predicate { requirement.test(CommandListenerWrapper_getBukkitSender.invoke(it) as CommandSender) }
    }

    override fun suggestions(command: ArgumentCommandNode<CommandSender, *>): SuggestionProvider<Any>? {
        TODO()
    }

    companion object {
        val CompletionProviders_CLASS = nms<Any>("CompletionProviders")
        val CompletionProviders_b = CompletionProviders_CLASS.getDeclaredField("b").apply { isAccessible = true }
        val CompletionProviders_c = CompletionProviders_CLASS.getDeclaredField("c").apply { isAccessible = true }
        val CompletionProviders_d = CompletionProviders_CLASS.getDeclaredField("d").apply { isAccessible = true }
        val CompletionProviders_e = CompletionProviders_CLASS.getDeclaredField("e").apply { isAccessible = true }

        val CommandListenerWrapper_CLASS = nms<Any>("CommandListenerWrapper")
        val CommandListenerWrapper_getBukkitSender = CommandListenerWrapper_CLASS.getDeclaredMethod("getBukkitSender")

        val CLIENT_SIDE = EnumMap<ClientSuggestionProvider, SuggestionProvider<*>>(ClientSuggestionProvider::class.java)

        init {
            CLIENT_SIDE[ClientSuggestionProvider.RECIPES] = CompletionProviders_b.get(null) as SuggestionProvider<*>
            CLIENT_SIDE[ClientSuggestionProvider.SOUNDS] = CompletionProviders_c.get(null) as SuggestionProvider<*>
            CLIENT_SIDE[ClientSuggestionProvider.BIOMES] = CompletionProviders_d.get(null) as SuggestionProvider<*>
            CLIENT_SIDE[ClientSuggestionProvider.ENTITIES] = CompletionProviders_e.get(null) as SuggestionProvider<*>
        }
    }
}