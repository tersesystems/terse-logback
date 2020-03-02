# Installation

Installation of Terse Logback requires you add the bintray repo to your build system.

### Maven

Add the following repository:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
          xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
    
    <profiles>
        <profile>
            <repositories>
                <repository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-tersesystems-maven</id>
                    <name>bintray</name>
                    <url>https://dl.bintray.com/tersesystems/maven</url>
                </repository>
            </repositories>
            <id>bintray</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>bintray</activeProfile>
    </activeProfiles>
</settings>
```

Create a subproject `logging` and make your main codebase depend on it, but only provide `slf4j-api` to the main codebase.

```xml
<dependency>
  <groupId>com.tersesystems.logback</groupId>
  <artifactId>logback-structured-config</artifactId>
  <version>$latestVersion</version>
  <type>pom</type>
</dependency>
```

### Gradle

Add the following resolver:

```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/tersesystems/maven" 
    }
}
```

Create a subproject `logging` and make your main codebase depend on it, but only provide `slf4j-api` to the main codebase.  In the logging project, add the following:

```
implementation 'com.tersesystems.logback:logback-structured-config:<latestVersion>'
```

### SBT

Create an SBT subproject and include it with your main build.

```
lazy val logging = (project in file("logging")).settings(
    resolvers += Resolver.bintrayRepo("tersesystems", "maven"),
    libraryDependencies += "com.tersesystems.logback" % "logback-structured-config" % "<latestVersion>"
)

lazy val impl = (project in file("impl")).settings(
  // all your code dependencies + slf4j-api
  libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
).dependsOn(logging)

lazy val root = project in file(".").aggregate(logging, impl)
```
