package com.github.xjcyan1de.kommandant

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture


interface MappedArgumentType<T> : ArgumentType<T> {
    val mapped: ArgumentType<*>

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> =
            listSuggestions(context.source, context, builder)

    fun <S> listSuggestions(source: S, context: CommandContext<S>?, builder: SuggestionsBuilder): CompletableFuture<Suggestions> =
            Suggestions.empty()
}