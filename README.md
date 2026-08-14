# Local project dependency cache baseline

Minimal, all-local reproducer for stale dependency advice from [Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) 3.18.0.

The baseline has two paths from `:failing-buildhealth-module` to `:transitive`:

```text
:failing-buildhealth-module -> :direct -> :transitive
                            \-------------> :transitive
```

The consumer declares both projects directly, so this should produce no dependency advice:

```shell
./gradlew clean :failing-buildhealth-module:projectHealth
```
