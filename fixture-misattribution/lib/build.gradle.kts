plugins {
    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.kotlin.jvm")
    `java-library`
    `java-test-fixtures`
}

kotlin {
    jvmToolchain(17)
}
