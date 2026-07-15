plugins {
    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

// -PleafEdge models an upstream PR landing in the middle module: it adds an api() edge to
// leaf and the source that exposes it. The consumer's build script is untouched by this.
if (providers.gradleProperty("leafEdge").isPresent) {
    sourceSets.main {
        kotlin.srcDir("src/withLeafEdge/kotlin")
    }
    dependencies {
        api(project(":stale-graph-cache:leaf"))
    }
}
