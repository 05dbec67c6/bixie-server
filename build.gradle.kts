plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com"
version = "0.0.1"

application {
    mainClass.set("com.ApplicationKt") // Assuming your file is Application.kt in package com
}

repositories {
    mavenCentral()
}

tasks {
    named("build"){
        mustRunAfter(named("clean"))
    }
    register("stage") {
        group = "build"
        description = "Runs clean and then build."
        dependsOn(named("build"))
        dependsOn(named("clean"))

    }
    val shadowJarProvider = named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java)

    register("herokuStage", Copy::class.java) { // Renamed to avoid conflict if you need both
        group = "build"
        description = "Stages the application JAR for Heroku deployment."
        dependsOn(shadowJarProvider)
        from(shadowJarProvider.flatMap { it.archiveFile })
        into(layout.buildDirectory.dir("libs"))
    }

    named("clean", Delete::class.java) {
        delete(layout.buildDirectory.dir("libs")) // For the Heroku stage output
    }
}


tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    manifest {
        attributes(
            Pair(
                "Main-Class",
                "com.ApplicationKt" // << MAKE SURE THIS IS YOUR ACTUAL MAIN CLASS
            )
        )
    }
    archiveBaseName.set("bixie-app-all.jar") // This should match your JAR name
    archiveClassifier.set("")  // Ensures no "-all" classifier
    archiveVersion.set("0.0.1")   // This should match your JAR version
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.websocket)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
