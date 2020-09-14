package com.github.xjcyan1de.kommandant

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException

fun interface Execution<T> : Command<T> {
    @Throws(CommandSyntaxException::class)
    fun execute(source: T, context: OptionalContext<T>)

    override fun run(context: CommandContext<T>): Int {
        execute(context.source, OptionalContext(context))
        return 1
    }
}