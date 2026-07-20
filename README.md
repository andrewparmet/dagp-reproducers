# DAGP reproducers

Minimal reproducers for incorrect-advice and cache-correctness bugs in the
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

## 3. `stale-graph-cache/`

`consumer -> middle -> leaf`, where the `middle -> leaf` edge and the consumer source using
a leaf class are both added by the `-PleafEdge` property (modeling an upstream module's PR
adding `api(project(":leaf"))` plus generated code in the consumer, with the consumer's own
`build.gradle.kts` untouched).

`onUsedTransitiveDependencies` is set to `severity("fail")` for the consumer, so
`projectHealth` is a real gate.

```
./gradlew :stale-graph-cache:consumer:projectHealth              # seed: passes, correctly
./gradlew :stale-graph-cache:consumer:projectHealth -PleafEdge   # PASSES: graphViewMain UP-TO-DATE, violation hidden
rm -rf stale-graph-cache/*/build                                 # then again:
./gradlew :stale-graph-cache:consumer:projectHealth -PleafEdge   # PASSES: graphViewMain FROM-CACHE, violation hidden
./gradlew :stale-graph-cache:consumer:projectHealth -PleafEdge --rerun-tasks --no-build-cache
```

The last command fails with the truth:

```
These transitive dependencies should be declared directly:
  implementation(project(":stale-graph-cache:leaf"))
```

The middle two commands pass, two flavors of the same defect: incrementality
(`UP-TO-DATE` even though the resolved compile classpath gained a project node) and the
build cache (a wiped `build/` restores the stale pre-edge graph). In both cases
`computeAdvice` runs against a stale `graph-compile.json` and emits no advice, so
`projectHealth`/`buildHealth` gates pass builds that violate the configured
`onUsedTransitiveDependencies { severity("fail") }` policy. The violation then surfaces on
whichever later build happens to miss the cache (in a large real build, the first PR after
any external-artifact bump), failing CI for an unrelated change.

Root cause: in
[GraphViewTask](https://github.com/autonomousapps/dependency-analysis-gradle-plugin/blob/main/src/main/kotlin/com/autonomousapps/tasks/GraphViewTask.kt)
the semantically dominant inputs are excluded from fingerprinting. `compileClasspathResult`
(the resolved `ResolvedComponentResult` graph) and `compileClasspathFileCoordinates` are
`@Internal`; the `@InputFiles` properties (`compileFiles`/`runtimeFiles`) are wired to
`externalArtifactsFor(...)`, which contains external artifacts only; and `declarations`
covers only the consumer's own build script. A transitive project edge added upstream
changes none of the tracked inputs, so the task's cache key (and up-to-date state) is
identical before and after the edge exists. Promoting the coordinate set to a tracked input
would invalidate exactly when the graph changes.

## 4. `android-fixture-rewrite-cycle/`

Two Android libraries reproduce a strict `fixDependencies` two-cycle. The consumer's
`testFixtures` source set exposes `FakeService` from the producer's test-fixtures capability
in its ABI, while using `Placeholder` from the producer's main capability only in a method
body. The correct declarations are therefore:

```groovy
testFixturesApi testFixtures(project(':android-fixture-rewrite-cycle:producer'))
testFixturesImplementation project(':android-fixture-rewrite-cycle:producer')
```

The reproducer starts with both declarations on `testFixturesApi`. DAGP correctly advises
demoting only the plain project dependency, but the Groovy build-script rewriter matches by
project coordinates without preserving the test-fixtures capability and demotes both. On
the next analysis DAGP correctly advises promoting only the test-fixtures dependency, but
the rewriter promotes both and returns the script to its original state.

```shell
./gradlew :android-fixture-rewrite-cycle:consumer:projectHealth --no-build-cache
./gradlew :android-fixture-rewrite-cycle:consumer:fixDependencies --no-build-cache
./gradlew :android-fixture-rewrite-cycle:consumer:projectHealth --no-build-cache
./gradlew :android-fixture-rewrite-cycle:consumer:fixDependencies --no-build-cache
```

Both `projectHealth` commands fail because this reproducer configures incorrect dependency
configurations as fatal. The first requests a plain-project demotion, the second requests a
test-fixtures promotion, and the two `fixDependencies` invocations alternate the build script
between the two incorrect states.
