# Installation

Installation is pretty straight-forward with the exception of the instrumentation agent, which is covered seperately.

Create a subproject `logging` and make your main codebase depend on it, but only provide `slf4j-api` to the main codebase.

### Maven

You should install at least `logback-classic`:

```xml
<dependency>
  <groupId>com.tersesystems.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>$latestVersion</version>
  <type>pom</type>
</dependency>
```

and if you want a "preconfigured" set up you can start with `logback-structured-config`:

```xml
<dependency>
  <groupId>com.tersesystems.logback</groupId>
  <artifactId>logback-structured-config</artifactId>
  <version>$latestVersion</version>
  <type>pom</type>
</dependency>
```

### Gradle

You should install at least `logback-classic`:

```
implementation 'com.tersesystems.logback:logback-classic:<latestVersion>'
```

and if you want a "preconfigured" set up you can start with `logback-structured-config`:

```
implementation 'com.tersesystems.logback:logback-structured-config:<latestVersion>'
```

### SBT

Same as Maven and Gradle.  Create an SBT subproject and include it with your main build.

```
lazy val logging = (project in file("logging")).settings(
  libraryDependencies += "com.tersesystems.logback" % "logback-structured-config" % "<latestVersion>"
)

lazy val impl = (project in file("impl")).settings(
  // all your code dependencies + slf4j-api
  libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
).dependsOn(logging)

lazy val root = project in file(".").aggregate(logging, impl)
```
