plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.21" apply false
    id("com.android.library") version "9.2.1" apply false
    id("com.autonomousapps.dependency-analysis") version "3.16.1"
}

dependencyAnalysis {
    usage {
        analysis {
            checkSuperClasses(true)
        }
    }
}
