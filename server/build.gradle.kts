import com.google.protobuf.gradle.*
import org.gradle.kotlin.dsl.provider.gradleKotlinDslOf

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    idea
    id("com.google.protobuf") version "0.8.18"
}

dependencies {

    // Protobuff dependencies
    implementation("com.google.protobuf:protobuf-java:3.19.4")

    // MongoDB dependece
    implementation("org.mongodb:mongo-java-driver:3.12.10")
}

repositories {
    maven("https://plugins.gradle.org/m2/")
    mavenLocal()
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.sd.beccacino.App")
}


protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
}
