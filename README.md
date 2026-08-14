# Stale advice with local project dependencies

Minimal, all-local reproducer for stale dependency advice from [Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) 3.18.0.

The baseline commit has two paths from `:consumer` to `:leaf`:

```text
:consumer -> :sdk -> :leaf
         \----------> :leaf
```

This commit removes the direct edge while leaving `:leaf` available through `:sdk`:

```text
:consumer -> :sdk -> :leaf
```

The consumer still instantiates `Leaf`, so DAGP should advise declaring `:leaf` directly.

Run the baseline first to seed cached task state, then analyze this commit with and without forced task execution:

```shell
git switch --detach HEAD^
./gradlew clean :consumer:projectHealth
git switch reproduce-stale-dependency-advice-cache
./gradlew clean :consumer:projectHealth
./gradlew clean :consumer:projectHealth --rerun-tasks
```

Correct behavior: both runs at the branch tip advise adding `implementation(project(":leaf"))`.

Stale-cache behavior: the normal run restores empty advice from the baseline, while `--rerun-tasks` reports the missing direct dependency.

The cache discrepancy was originally observed when CI restored Gradle home and project workspace state from an earlier revision. A local Gradle build cache may invalidate correctly, so reproducing the false green can require the same project-workspace cache restoration used by CI.
