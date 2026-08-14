# Stale advice with local project dependencies

Minimal, all-local reproducer for stale dependency advice from [Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) 3.18.0.

The baseline commit has two paths from `:failing-buildhealth-module` to `:transitive`:

```text
:failing-buildhealth-module -> :direct -> :transitive
                            \-------------> :transitive
```

The failing module declares both projects directly, so the baseline produces no dependency advice.

This commit removes the direct edge while leaving `:transitive` available through `:direct`:

```text
:failing-buildhealth-module -> :direct -> :transitive
```

The failing module still instantiates `Transitive`, so DAGP should advise declaring `:transitive` directly.

Run the baseline first to seed cached task state, then analyze this commit with and without forced task execution:

```shell
git switch --detach HEAD^
./gradlew clean :failing-buildhealth-module:projectHealth
git switch reproduce-stale-dependency-advice-cache
./gradlew clean :failing-buildhealth-module:projectHealth
./gradlew clean :failing-buildhealth-module:projectHealth --rerun-tasks
```

Correct behavior: both runs at the branch tip advise adding `implementation(project(":transitive"))`.

Stale-cache behavior: the normal run restores empty advice from the baseline, while `--rerun-tasks` reports the missing direct dependency.

The cache discrepancy was originally observed when CI restored Gradle home and project workspace state from an earlier revision. A local Gradle build cache may invalidate correctly, so reproducing the false green can require the same project-workspace cache restoration used by CI.
