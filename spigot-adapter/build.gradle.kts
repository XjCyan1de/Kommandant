plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("com.mojang", "brigadier", "1.0.17")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.3-R0.1-SNAPSHOT")

    api(parent!!)
}
