plugins {
    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

// The extra source dir models generated code (e.g. a Dagger component) that starts using a
// leaf class once the upstream edge exists. Note the consumer's *declarations* never change.
if (providers.gradleProperty("leafEdge").isPresent) {
    sourceSets.main {
        kotlin.srcDir("src/withLeafEdge/kotlin")
    }
}

dependencies {
    implementation(project(":stale-graph-cache:middle"))
}
