plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    maven("https://libraries.minecraft.net/")
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven")

    dependencies {
        compileOnly("com.mojang", "brigadier", "1.0.17")
    }
}