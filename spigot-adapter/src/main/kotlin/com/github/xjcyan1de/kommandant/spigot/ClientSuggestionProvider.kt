package com.github.xjcyan1de.kommandant.spigot

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

enum class ClientSuggestionProvider : SuggestionProvider<CommandSender> {
    BIOMES, ENTITIES, RECIPES, SOUNDS;

    override fun getSuggestions(context: CommandContext<CommandSender>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return builder.buildFuture()
    }
}