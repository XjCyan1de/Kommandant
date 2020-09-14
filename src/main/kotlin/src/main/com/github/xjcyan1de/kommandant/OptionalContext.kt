package src.main.com.github.xjcyan1de.kommandant

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.ParsedArgument
import com.mojang.brigadier.context.ParsedCommandNode
import com.mojang.brigadier.context.StringRange
import java.lang.invoke.MethodHandles

class OptionalContext<T>(
        private val context: CommandContext<T>
) : CommandContext<T?>(null, null, null, null, null, null, null, null, null, false) {
    private var arguments: Map<String, ParsedArgument<T, *>>? = null

    companion object {
        val ARGUMENTS = MethodHandles.privateLookupIn(CommandContext::class.java, MethodHandles.lookup())
                .findVarHandle(CommandContext::class.java, "arguments", MutableMap::class.java)
    }

    fun <V> getOptionalArgument(name: String, type: Class<V>): V? {
        return getOptionalArgument(name, type, null)
    }

    fun <V> getOptionalArgument(name: String, type: Class<V>, value: V?): V? {
        if (arguments == null) {
            arguments = ARGUMENTS.get(context) as Map<String, ParsedArgument<T, *>>
        }
        val argument = arguments!![name]
        return if (argument != null) {
            getArgument(name, type)
        } else {
            value
        }
    }

    override fun copyFor(source: T?): OptionalContext<T?> {
        return OptionalContext(context.copyFor(source))
    }

    override fun getChild(): CommandContext<T?> {
        return context.child
    }

    override fun getLastChild(): CommandContext<T?> {
        return context.lastChild
    }

    override fun getCommand(): Command<T?> {
        return context.command
    }

    override fun getSource(): T? {
        return context.source
    }

    override fun <V> getArgument(name: String, type: Class<V>): V {
        return context.getArgument(name, type)
    }

    override fun getRedirectModifier(): RedirectModifier<T?> {
        return context.redirectModifier
    }

    override fun getRange(): StringRange {
        return context.range
    }

    override fun getInput(): String {
        return context.input
    }

    override fun getNodes(): List<ParsedCommandNode<T?>> {
        return context.nodes
    }

    override fun isForked(): Boolean {
        return context.isForked
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is OptionalContext<*>) {
            return false
        }
        return context == other.context
    }

    override fun hashCode(): Int = 31 * context.hashCode()
}