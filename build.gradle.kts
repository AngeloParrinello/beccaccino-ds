
plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

subprojects {

    apply(plugin = "java")

    repositories {
        // Use Maven Central for resolving dependencies.
        mavenCentral()
    }

    dependencies {

        // RabbitMQ dependencies
        implementation("com.rabbitmq:amqp-client:5.14.2")
        runtimeOnly("org.slf4j:slf4j-nop:1.7.32")

        // Use JUnit Jupiter for testing.
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
        // This dependency is used by the application.
        implementation("com.google.guava:guava:30.1.1-jre")
    }

    tasks.test {
        // Use JUnit Platform for unit tests.
        useJUnitPlatform()
    }
}

