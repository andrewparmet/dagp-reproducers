plugins {
    id("com.android.library") version "9.2.1"
    id("com.autonomousapps.dependency-analysis") version "3.18.0"
}

android {
    namespace = "com.example.consumer"
    compileSdk = 36
}

dependencies {
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.threeten:threetenbp:1.6.0")
}

dependencyAnalysis {
    usage {
        analysis {
            checkSuperClasses(true)
        }
    }
}
