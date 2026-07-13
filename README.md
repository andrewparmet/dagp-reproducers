# DAGP reproducers

Minimal reproducers for two incorrect-advice bugs in the
[Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin)
(3.16.1, Gradle 9.5.1, Kotlin 2.3.21, AGP 9.2.1).

Each reproducer is an independent group of modules in this multi-module build.

## Reproducing

```
./gradlew build            # compiles as declared
./gradlew buildHealth      # emits the incorrect advice below
```

## 1. `fixture-misattribution/`

An Android library consumer declares
`testImplementation(testFixtures(project(":fixture-misattribution:lib")))` and its unit test
sources use only `FakeModelSyncService`, a class that exists solely in the lib's
`testFixtures` source set.

DAGP advises:

```
Advice for :fixture-misattribution:consumer
These transitive dependencies should be declared directly:
  testImplementation(project(":fixture-misattribution:lib"))
```

The advice is wrong: the only class used comes from the already-declared test-fixtures
capability, not from the lib's main capability. The usage report attributes
`com.example.lib.fixtures.FakeModelSyncService` to *both* capabilities.

Root cause: for local Android consumers, the artifact for the lib's main capability is a
classes directory ending in `kotlin/main`, which
[ArtifactsReportTask](https://github.com/autonomousapps/dependency-analysis-gradle-plugin/blob/main/src/main/kotlin/com/autonomousapps/tasks/ArtifactsReportTask.kt)
collapses to the grandparent `build/classes` (the workaround for #948). `build/classes` also
contains `kotlin/testFixtures` once fixtures compile, so the main capability absorbs the
fixture classes. (JVM consumers are unaffected: they resolve the producer's jars, which stay
separated.) In larger builds the same collapse makes DAGP advise *removing* the
`testFixtures(...)` declaration outright, which breaks test compilation:

```
e: ConsumerTest.kt: Unresolved reference 'fixtures'.
e: ConsumerTest.kt: Unresolved reference 'FakeModelSyncService'.
```

The advice also flip-flops with build state, since it depends on whether the fixture classes
have been compiled into `build/classes` at analysis time. The lib's main source set must be
non-empty for the collapse to trigger (`Placeholder.kt`).

A workaround that suppresses the bad advice is relocating the fixture output outside
`build/classes` in the producer:

```kotlin
sourceSets.named("testFixtures") {
    java.destinationDirectory.set(layout.buildDirectory.dir("testFixtures-classes/java"))
}
kotlin.sourceSets.named("testFixtures") {
    kotlin.destinationDirectory.set(layout.buildDirectory.dir("testFixtures-classes/kotlin"))
}
```

## 2. `supertype-demotion/`

- `supertype-lib` provides `BaseApiClient`, with a concrete `get()` method.
- `client-lib` provides `GuestClient : BaseApiClient()`, declaring supertype-lib as
  `implementation` (modeling a prebuilt external artifact whose POM has the supertype
  provider at runtime scope; DAGP is not applied to it).
- `consumer` declares both and calls `GuestClient().guests()`, an inherited-API interaction
  that requires `BaseApiClient` on the compile classpath. Its only compile-classpath
  provider is the direct `supertype-lib` declaration.

DAGP advises, even with `checkSuperClasses(true)`:

```
Advice for :supertype-demotion:consumer
Unused dependencies which should be removed:
  implementation(project(":supertype-demotion:supertype-lib"))
```

`reason` reports "(no usages)" for supertype-lib. Applying the advice breaks main
compilation:

```
e: App.kt: Cannot access 'BaseApiClient' which is a supertype of 'GuestClient'.
Check your module classpath for missing or conflicting dependencies.
```

DAGP credits the supertype usage to `client-lib` (the direct provider of `GuestClient`) but
not to the dependency that actually supplies `BaseApiClient` to the compile classpath.
