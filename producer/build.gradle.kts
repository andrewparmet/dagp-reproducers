plugins {
    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.kotlin.jvm")
    `java-library`
    `java-test-fixtures`
}

val relocateTestFixtures = providers.gradleProperty("relocateTestFixtures").map(String::toBoolean).getOrElse(false)

kotlin {
    jvmToolchain(17)
    if (relocateTestFixtures) {
        sourceSets.named("testFixtures") {
            kotlin.destinationDirectory.set(layout.buildDirectory.dir("testFixtures-classes/kotlin"))
        }
    }
}

if (relocateTestFixtures) {
    sourceSets.named("testFixtures") {
        java.destinationDirectory.set(layout.buildDirectory.dir("testFixtures-classes/java"))
    }
}
