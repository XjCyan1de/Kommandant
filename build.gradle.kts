plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://gitlab.com/XjCyan1de/maven-repo/-/raw/master/")
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    group = "com.github.xjcyan1de"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenLocal()
        jcenter()
    }

    dependencies {
        compileOnly("com.mojang", "brigadier", "1.0.17")
        compileOnly("com.destroystokyo.paper", "paper", "1.16.4-R0.1-SNAPSHOT")
    }

    publishing {
        val sourcesJar by tasks.creating(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.getByName("main").allSource)
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = this@allprojects.group.toString()
                artifactId = this@allprojects.name
                version = this@allprojects.version.toString()
                artifact(sourcesJar)
                from(components["java"])
            }
        }
    }
}