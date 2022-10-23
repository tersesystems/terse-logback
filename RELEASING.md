## Release

To make sure everything works:

```bash
./gradlew clean build check
```

To format everything using [Spotless](https://github.com/diffplug/spotless/tree/master/plugin-gradle):

```bash
./gradlew spotlessApply
```

First, try publishing to maven local:

```bash
./gradlew publishToMavenLocal
```

If that works, then publish to Sonatype's staging repository and close:

```bash
./gradlew publishToSonatype closeSonatypeStagingRepository
```

Inspect this in Sonatype OHSSH repository.  Delete the staging repository after inspection.

And then to promote it:

```bash
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

If it looks weird that you have to specify "publishToSonatype" with another task, that's because [it is weird](https://github.com/gradle-nexus/publish-plugin/issues/19).

## Gradle Signing

If you run into errors with signing doing a `publishToSonaType`, this is common and underdocumented.

```
No value has been specified for property 'signatory.keyId'.
```

For the `signatory.keyId` error message, you need to set `signing.gnupg.keyName` if you
are using GPG 2.1 and a Yubikey 4.

https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
https://github.com/gradle/gradle/pull/1703/files#diff-6c52391bbdceb4cca64ce7b03e78212fR6

Note you need to use `gpg -K` and pick only the LAST EIGHT CHARS of the public signing key.

> signing.gnupg.keyName = 5F798D53

### PinEntry

Also note that if you are using a Yubikey, it'll require you to type in a PIN, which screws up Gradle.

```
gpg: signing failed: No pinentry
```

So you need to use pinentry-mode loopback, which is helpfully supplied by passphrase.

- https://github.com/sbt/sbt-pgp/pull/142
- https://wiki.archlinux.org/index.php/GnuPG#Unattended_passphrase
- https://github.com/gradle/gradle/pull/1703/files#diff-790036df959521791fdafe474b673924

You want this specified only the command line, i.e.

> $ HISTCONTROL=ignoreboth ./gradlew publishToMavenLocal -Psigning.gnupg.passphrase=$PGP_PASSPHRASE --info

### Cannot Allocate Memory

gpg can't be run in parallel.  You'll get this error message.

```
gpg: signing failed: Cannot allocate memory
```
[Gradle is not smart enough to disable this](https://github.com/gradle/gradle/issues/12167). 

Do not use `-Porg.gradle.parallel=false` and don't use `--parallel` when publishing.

## Documentation

Documentation is done with [gradle-mkdocs-plugin](https://xvik.github.io/gradle-mkdocs-plugin/) and works best on Linux.

Need to have [Python 3.8](https://tech.serhatteker.com/post/2019-12/upgrade-python38-on-ubuntu/), virtualenv is not enough.

To see documentation:

```bash
./gradlew mkdocsServe --no-daemon
```

To deploy documentation:

```bash
./gradlew mkdocsPublish
```