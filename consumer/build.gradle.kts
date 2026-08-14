plugins {
    id("com.autonomousapps.dependency-analysis")
    `java-library`
}

dependencies {
    implementation(project(":sdk"))
    implementation(project(":leaf"))
}
