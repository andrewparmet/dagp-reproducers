# Local dependency cache baseline

Baseline for a [Dependency Analysis Gradle Plugin](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) cache reproducer.

```text
:consumer -> :sdk -> :leaf
         \----------> :leaf
```

The consumer uses both local projects and declares both directly, so this should produce no dependency advice:

```shell
./gradlew clean :consumer:projectHealth
```
