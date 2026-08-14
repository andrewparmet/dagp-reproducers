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

include(":failing-buildhealth-module")
include(":direct")
include(":transitive")
