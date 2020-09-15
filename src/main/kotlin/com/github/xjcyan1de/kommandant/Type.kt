package com.github.xjcyan1de.kommandant

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

interface Type<T> : ArgumentType<T> {
    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return listSuggestions(context.source, context, builder)
    }

    fun <S> listSuggestions(source: S, context: CommandContext<S>?, builder: SuggestionsBuilder?): CompletableFuture<Suggestions> {
        return Suggestions.empty()
    }

    fun mapped(): ArgumentType<*>?
}