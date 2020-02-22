## Release

To make sure everything works:

```bash
./gradlew clean build check
```

To format everything using [Spotless](https://github.com/diffplug/spotless/tree/master/plugin-gradle):

```bash
./gradlew spotlessApply
```

Releases are handled using [shipkit](https://github.com/mockito/shipkit):

```bash
./gradlew testRelease
```

And then to run the release, which will increment the version number in `version.properties`:

```bash
./gradlew performRelease
```