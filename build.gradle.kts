plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.21" apply false
    id("com.android.library") version "9.2.1" apply false
    id("com.autonomousapps.dependency-analysis") version "3.16.1"
}

dependencyAnalysis {
    issues {
        // Scoped to the cache reproducer so the other reproducers' buildHealth stays
        // non-fatal. This mirrors a real buildHealth gate: undeclared transitive usage
        // fails the build -- except when a stale cached graph hides it.
        project(":stale-graph-cache:consumer") {
            onUsedTransitiveDependencies {
                severity("fail")
            }
        }
    }
    usage {
        analysis {
            checkSuperClasses(true)
        }
    }
}
