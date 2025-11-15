plugins {
    kotlin("jvm") version "2.2.0"
    idea
    id("com.gradleup.shadow") version "9.0.0-rc1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

version = "1.0-SNAPSHOT"
group = "dev.fallow.stardew"
description = "Stardew Valley clone"
val pluginEntry = "dev.fallow.stardew.StardewPlugin"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")     // Kotlin stdlib
    implementation("com.marcusslover:plus:4.4.1-SNAPSHOT")          // Plus
}

kotlin {
    jvmToolchain(21)
}

tasks {
    shadowJar {
        val prefix = "dev.fallow.relocated"

        mergeServiceFiles() // allow services to load (i.e. postgres driver)
        relocate("kotlin", "$prefix.kotlin")
        relocate("com.marcusslover.plus.lib", "$prefix.com.marcusslover.plus.lib")
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "api" to "1.21.8",
            "pluginEntry" to pluginEntry,
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    assemble {
//        dependsOn(reobfJar)
    }
    build {
        dependsOn(shadowJar)
//        dependsOn(reobfJar)
    }
}
