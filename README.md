# Missing test-fixture dependency advice

Run:

```sh
./gradlew :consumer:compileTestFixturesKotlin :fixture-bridge:compileTestKotlin
./gradlew fixDependencies
./gradlew :consumer:compileTestFixturesKotlin :fixture-bridge:compileTestKotlin --continue
```

Compilation succeeds before `fixDependencies`. Afterward, both tasks fail with
`Unresolved reference 'createModel'` because required fixture dependencies are missing.
