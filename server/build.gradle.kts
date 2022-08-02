import com.google.protobuf.gradle.*

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    idea
    id("com.google.protobuf") version "0.8.18"
}

repositories {
    maven("https://plugins.gradle.org/m2/")
    maven ("https://maven-central.storage-download.googleapis.com/maven2/" )
    mavenCentral()
    mavenLocal()
}

val protobufVersion = "3.19.4"
val protocVersion = protobufVersion

dependencies {

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
    
    //implementation("com.google.protobuf:protobuf-javalite:${protobufVersion}")

    // examples/advanced need this for JsonFormat
    implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")

    // Protobuff dependencies
    implementation("com.google.protobuf:protobuf-java:${protobufVersion}")

    // MongoDB dependece
    //implementation("org.mongodb:mongo-java-driver:3.12.10")
    //implementation("org.mongodb:mongodb-driver-core:4.2.1")
    implementation("org.mongodb:mongodb-driver-sync:4.5.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protocVersion}"
    }
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.sd.beccacino.Launcher")
}



