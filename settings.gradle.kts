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
