pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "dagp-reproducers"

include(":fixture-misattribution:lib")
include(":fixture-misattribution:consumer")
include(":supertype-demotion:supertype-lib")
include(":supertype-demotion:client-lib")
include(":supertype-demotion:consumer")
include(":stale-graph-cache:leaf")
include(":stale-graph-cache:middle")
include(":stale-graph-cache:consumer")

buildCache {
    local {
        // A relocatable directory keeps the cache-poisoning reproducer self-contained and
        // trivially resettable (rm -rf build-cache).
        directory = File(rootDir, "build-cache")
    }
}
