plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10" apply false
    id("com.android.library") version "9.3.1" apply false
    id("com.autonomousapps.dependency-analysis") version "3.18.0"
}

dependencyAnalysis {
    usage {
        analysis {
            checkSuperClasses(true)
        }
    }
}
