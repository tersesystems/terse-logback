
## Release

I can never remember how to release projects, so I'm using [Kordamp Gradle Plugins](https://aalmiray.github.io/kordamp-gradle-plugins/) to do most of the work.  I've added some properties to deal with signing artifacts with gpg2 and a Yubikey 4 and staging on Bintray.

You will need to publish to bintray, so be sure to have the following in `$HOME/.gradle/gradle.properties`:

```
bintrayUsername = [CENSORED]
bintrayApiKey = [CENSORED]
```

To publish to Maven Local repository, use the [builtin](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:install):

```bash
./gradlew publishToMavenLocal
```

To stage on Bintray:

```bash
HISTCONTROL=ignoreboth ./gradlew clean bintrayUpload -Pversion=x.x.x -Psigning.gnupg.passphrase=123456 --info
```

If it [all goes south](https://dzone.com/articles/why-i-never-use-maven-release) then it may be time to move to [something else](https://axelfontaine.com/blog/dead-burried.html) rather than `maven-release-plugin`.
