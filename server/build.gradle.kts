plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
//    kotlin("jvm") // Ensure the JVM plugin is applied
//    id("io.ktor.jvm") version "YOUR_KTOR_VERSION" // Apply the Ktor JVM plugin
//    id("org.jetbrains.kotlin.plugin.serialization") version "YOUR_KOTLIN_VERSION" // For serialization
//    application // Apply the application plugin to make it executable
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // Ktor server engine (Netty is a good choice)
    implementation("io.ktor:ktor-server-netty:YOUR_KTOR_VERSION")
    // Ktor WebSockets feature
    implementation("io.ktor:ktor-server-websockets:YOUR_KTOR_VERSION")
    // Ktor Content Negotiation and Serialization (for sending/receiving structured data)
    implementation("io.ktor:ktor-server-content-negotiation:YOUR_KTOR_VERSION")
    implementation("io.ktor:ktor-serialization-kotlinx-json:YOUR_KTOR_VERSION")
    // Kotlinx Serialization Runtime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:YOUR_SERIALIZATION_VERSION")

    // Logging (useful for debugging)
    implementation("io.ktor:ktor-server-call-logging:YOUR_KTOR_VERSION")
    implementation("ch.qos.logback:logback-classic:YOUR_LOGBACK_VERSION")

    // Kotlin standard library
    implementation(kotlin("stdlib-jdk8"))

    // Ktor server tests (Optional but good practice)
    testImplementation("io.ktor:ktor-server-tests-jvm:YOUR_KTOR_VERSION")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:YOUR_KOTLIN_VERSION")
}
