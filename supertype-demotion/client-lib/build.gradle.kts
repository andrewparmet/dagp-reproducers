plugins {
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // client-lib models a prebuilt external artifact: its POM would list supertype-lib at
    // runtime scope, and DAGP cannot rewrite it. Deliberately not exposed via api().
    implementation(project(":supertype-demotion:supertype-lib"))
}
