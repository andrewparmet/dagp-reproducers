plugins {
    id("com.autonomousapps.dependency-analysis")
    id("org.jetbrains.kotlin.jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // consumer calls methods on GuestClient, whose supertype BaseApiClient must be on the
    // compile classpath. DAGP does not credit supertype-lib with that usage (even with
    // checkSuperClasses(true)) and advises removing it, which breaks main compilation:
    // "Cannot access 'BaseApiClient' which is a supertype of 'GuestClient'".
    implementation(project(":supertype-demotion:supertype-lib"))
    implementation(project(":supertype-demotion:client-lib"))
}
