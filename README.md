# Stale advice with local project dependencies

Minimal, all-local reproducer for stale dependency advice from [Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) 3.18.0.

By default, the build has two paths from `:failing-buildhealth-module` to `:transitive`:

```text
:failing-buildhealth-module -> :direct -> :transitive
                            \-------------> :transitive
```

The failing module declares both projects directly, so the default configuration produces no dependency advice.

The `rewireDependency=true` configuration removes the direct edge while leaving `:transitive` available through `:direct`:

```text
:failing-buildhealth-module -> :direct -> :transitive
```

The failing module still instantiates `Transitive`, so DAGP should advise declaring `:transitive` directly.

Run the default configuration first to seed cached task state, then enable the property with and without forced task execution:

```shell
./gradlew clean :failing-buildhealth-module:projectHealth
./gradlew clean :failing-buildhealth-module:projectHealth -PrewireDependency=true
./gradlew clean :failing-buildhealth-module:projectHealth -PrewireDependency=true --rerun-tasks
```

Correct behavior: both property-enabled runs advise adding `implementation(project(":transitive"))`.

Stale-cache behavior: the normal run restores empty advice from the baseline, while `--rerun-tasks` reports the missing direct dependency.

The cache discrepancy was originally observed when CI restored Gradle home and project workspace state from an earlier revision. A local Gradle build cache may invalidate correctly, so reproducing the false green can require the same project-workspace cache restoration used by CI.
