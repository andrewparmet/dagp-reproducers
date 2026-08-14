plugins {
    id("com.autonomousapps.dependency-analysis")
    `java-library`
}

dependencies {
    implementation(project(":direct"))
    if (!providers.gradleProperty("rewireDependency").map(String::toBoolean).getOrElse(false)) {
        implementation(project(":transitive"))
    }
}
