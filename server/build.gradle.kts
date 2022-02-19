plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

dependencies {

    // MongoDB dependece
    implementation("org.mongodb:mongo-java-driver:3.12.10")
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.sd.beccacino.App")
}
