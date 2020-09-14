package com.github.xjcyan1de.kommandant

interface CommandWrapper {
    fun getName(): String

    fun getLabel(): String

    fun getAliases(): List<String>
}