package com.github.xjcyan1de.kommandant.node

import com.github.xjcyan1de.kommandant.Execution
import com.github.xjcyan1de.kommandant.OptionalContext
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode

@Suppress("UNCHECKED_CAST")
abstract class NodeBuilder<T, B : NodeBuilder<T, B>> : ArgumentBuilder<T, B>() {
    fun executes(command: OptionalContext<T>.(T)->Unit): B = apply {
        executes { source, context ->
            command(context, source)
        }
    } as B

    fun executes(command: Execution<T>): B = apply {
        executes(command as Command<T>)
    } as B

    fun optionaly(builder: ArgumentBuilder<T, *>): B = apply {
        optionaly(builder.build())
    } as B

    fun optionaly(node: CommandNode<T>): B = apply {
        then(node)
        node.children.forEach {
            then(it)
        }
    } as B

    fun literal(name: String, vararg aliases: String, block: Literal.Builder<T>.() -> Unit = {}) =
            then(Literal.of<T>(name).alias(*aliases).apply(block))

    fun <V> argument(name: String, argumentType: ArgumentType<V>, block: Argument.Builder<T, V>.() -> Unit = {}) =
            then(Argument.of<T, V>(name, argumentType).apply(block))

    fun word(name: String, block: Argument.Builder<T, String>.() -> Unit = {}) =
            then(Argument.of<T, String>(name, StringArgumentType.word()).apply(block))

    fun string(name: String, block: Argument.Builder<T, String>.() -> Unit = {}) =
            then(Argument.of<T, String>(name, StringArgumentType.string()).apply(block))

    fun greedyString(name: String, block: Argument.Builder<T, String>.() -> Unit = {}) =
            then(Argument.of<T, String>(name, StringArgumentType.greedyString()).apply(block))

    fun boolean(name: String, block: Argument.Builder<T, Boolean>.() -> Unit = {}) =
            then(Argument.of<T, Boolean>(name, BoolArgumentType.bool()).apply(block))

    fun double(name: String, min: Double = Double.MIN_VALUE, max: Double = Double.MAX_VALUE, block: Argument.Builder<T, Double>.() -> Unit = {}) =
            then(Argument.of<T, Double>(name, DoubleArgumentType.doubleArg(min, max)).apply(block))

    fun float(name: String, min: Float = Float.MIN_VALUE, max: Float = Float.MAX_VALUE, block: Argument.Builder<T, Float>.() -> Unit = {}) =
            then(Argument.of<T, Float>(name, FloatArgumentType.floatArg(min, max)).apply(block))

    fun int(name: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE, block: Argument.Builder<T, Int>.() -> Unit = {}) =
            then(Argument.of<T, Int>(name, IntegerArgumentType.integer(min, max)).apply(block))

    fun long(name: String, min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE, block: Argument.Builder<T, Long>.() -> Unit = {}) =
            then(Argument.of<T, Long>(name, LongArgumentType.longArg(min, max)).apply(block))
}