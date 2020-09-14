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

    repositories {
        mavenLocal()
        jcenter()
    }

    dependencies {
//        compileOnly("com.mojang", "brigadier", "1.0.17")
        compileOnly("com.destroystokyo.paper", "paper", "1.16.3-R0.1-SNAPSHOT")
    }
}