package com.github.xjcyan1de.kommandant.dispatcher

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.server.v1_16_R3.CommandListenerWrapper
import net.minecraft.server.v1_16_R3.CompletionProviders
import org.bukkit.command.CommandSender
import com.github.xjcyan1de.kommandant.ClientSuggestionProvider
import com.github.xjcyan1de.kommandant.Type
import com.github.xjcyan1de.kommandant.util.Mapper
import java.util.*
import java.util.function.Predicate

internal class SpigotMapper(private val dispatcher: CommandDispatcher<CommandSender>) : Mapper<CommandSender, CommandListenerWrapper>() {
    companion object {
        val CLIENT_SIDE: MutableMap<ClientSuggestionProvider, SuggestionProvider<CommandListenerWrapper>> = EnumMap(ClientSuggestionProvider::class.java)

        init {
            CLIENT_SIDE[ClientSuggestionProvider.RECIPES] = CompletionProviders.b
            CLIENT_SIDE[ClientSuggestionProvider.SOUNDS] = CompletionProviders.c
            CLIENT_SIDE[ClientSuggestionProvider.BIOMES] = CompletionProviders.d
            CLIENT_SIDE[ClientSuggestionProvider.ENTITIES] = CompletionProviders.e
        }
    }

    override fun type(command: ArgumentCommandNode<CommandSender, *>): ArgumentType<*>? {
        val type = command.type
        return if (type is Type<*>) type.mapped() else type
    }

    override fun requirement(command: CommandNode<CommandSender>?): Predicate<CommandListenerWrapper> {
        val requirement = command!!.requirement
        return if (requirement == null) TRUE as Predicate<CommandListenerWrapper> else Predicate { listener: CommandListenerWrapper -> requirement.test(listener.bukkitSender) }
    }

    override fun suggestions(command: ArgumentCommandNode<CommandSender, *>): SuggestionProvider<CommandListenerWrapper>? {
        val type = command.type
        val suggestor = command.customSuggestions
        if (type !is Type<*> && suggestor == null) {
            return null
        } else if (suggestor == null) {
            return reparse(type as Type<*>)
        }
        val client = CLIENT_SIDE[suggestor]
        return client ?: reparse(suggestor)
    }

    fun reparse(type: Type<*>): SuggestionProvider<CommandListenerWrapper> {
        return SuggestionProvider { context: CommandContext<CommandListenerWrapper>, suggestions: SuggestionsBuilder? ->
            val sender = context.source.bukkitSender
            var input = context.input
            input = if (input.length <= 1) "" else input.substring(1)
            val reparsed = dispatcher.parse(input, sender).context.build(context.input)
            type.listSuggestions(reparsed, suggestions!!)
        }
    }

    fun reparse(suggestor: SuggestionProvider<CommandSender?>): SuggestionProvider<CommandListenerWrapper> {
        return SuggestionProvider { context: CommandContext<CommandListenerWrapper>, suggestions: SuggestionsBuilder? ->
            val sender = context.source.bukkitSender
            var input = context.input
            input = if (input.length <= 1) "" else input.substring(1)
            val reparsed = dispatcher.parse(input, sender).context.build(context.input)
            suggestor.getSuggestions(reparsed, suggestions)
        }
    }
}