plugins {
    id("com.autonomousapps.dependency-analysis")
    id("com.android.library")
}

android {
    namespace = "com.example.androidproducer"
    compileSdk = 36

    testFixtures {
        enable = true
    }
}

kotlin {
    jvmToolchain(17)
}
