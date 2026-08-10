plugins {
    id("com.autonomousapps.dependency-analysis")
    id("com.android.library")
}

android {
    namespace = "com.example.consumer"
    compileSdk = 36
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(testFixtures(project(":producer")))
}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}
