plugins {
    id("com.autonomousapps.dependency-analysis")
    `java-library`
}

dependencies {
    implementation(project(":direct"))
    implementation(project(":transitive"))
}
