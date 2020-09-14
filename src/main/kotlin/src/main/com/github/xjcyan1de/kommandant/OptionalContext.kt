package src.main.com.github.xjcyan1de.kommandant

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.ParsedArgument
import com.mojang.brigadier.context.ParsedCommandNode
import com.mojang.brigadier.context.StringRange
import java.lang.invoke.MethodHandles
import kotlin.reflect.KClass

class OptionalContext<T>(
        private val context: CommandContext<T>
) : CommandContext<T>(null, null, null, null, null, null, null, null, null, false) {
    private var arguments: Map<String, ParsedArgument<T, *>>? = null

    companion object {
        val ARGUMENTS = MethodHandles.privateLookupIn(CommandContext::class.java, MethodHandles.lookup())
                .findVarHandle(CommandContext::class.java, "arguments", MutableMap::class.java)
    }

    fun <V> getOptionalArgument(name: String, type: Class<V>): V? = getOptionalArgument(name, type, null)
    fun <V : Any> getOptionalArgument(name: String, type: KClass<V>): V? = getOptionalArgument(name, type.java, null)
    inline fun <reified V : Any> getOptionalArgument(name: String): V? = getOptionalArgument(name, V::class.java, null)

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
    fun <V : Any> getOptionalArgument(name: String, type: KClass<V>, value: V?): V? = getOptionalArgument(name, type.java, value)
    inline fun <reified V : Any> getOptionalArgument(name: String, value: V?): V? = getOptionalArgument(name, V::class.java, value)

    override fun copyFor(source: T): OptionalContext<T> = OptionalContext(context.copyFor(source))

    override fun getChild(): CommandContext<T> = context.child

    override fun getLastChild(): CommandContext<T> = context.lastChild

    override fun getCommand(): Command<T> = context.command

    override fun getSource(): T = context.source

    override fun <V> getArgument(name: String, type: Class<V>): V = context.getArgument(name, type)
    fun <V : Any> getArgument(name: String, type: KClass<V>): V = context.getArgument(name, type.java)
    inline fun <reified V : Any> getArgument(name: String): V = getArgument(name, V::class.java)

    override fun getRedirectModifier(): RedirectModifier<T> = context.redirectModifier

    override fun getRange(): StringRange = context.range

    override fun getInput(): String = context.input

    override fun getNodes(): List<ParsedCommandNode<T>> = context.nodes

    override fun isForked(): Boolean = context.isForked

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