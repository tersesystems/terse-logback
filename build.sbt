import Dependencies._

// SBT is not what people are used to for Java projects, but it's what I know.
//
// "brew install sbt" to install sbt.
//
// "sbt run" to run the example.
//
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.tersesystems",
      crossPaths := false,
      autoScalaLibrary := false,      
      version      := "0.1.0-SNAPSHOT",
      testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
      libraryDependencies += junitInterface % Test,
    )),
    name := "terse-logback-root",
    publish / skip := true,

    mainClass in Compile := (mainClass in Compile in example).value
  ).aggregate(classic, example, guice).dependsOn(example) // dependsOn for the mainClass

// A "classic" project with the underlying appenders and infrastructure.
// Create your own github repository for something like this, and publish it to your artifactory or locally.
// THIS IS NOT INTENDED TO BE A DROP IN REPLACEMENT, but an example of structured logging with Logback.
lazy val classic = (project in file("classic")).
  settings(
    name := "terse-logback-classic",
    // https://mvnrepository.com/artifact/net.logstash.logback/logstash-logback-encoder
    libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "org.fusesource.jansi" % "jansi" % "1.17.1", // color on windows
    libraryDependencies += "com.typesafe" % "config" % "1.3.3",
    libraryDependencies += "org.slf4j" % "jul-to-slf4j" % "1.7.25",
    libraryDependencies += "org.codehaus.janino" % "janino" % "3.0.11"
  )

// Your end user project.  Add a "logback.conf" file and a library dependency on your base project, and you're done.
lazy val example = (project in file("example")).
  settings(
    name := "terse-logback-example",
    publish / skip := true,
    mainClass := Some("example.Main"),
    libraryDependencies += "net.mguenther.idem" % "idem-core" % "0.1.0"
  ).dependsOn(classic).aggregate(classic)


// Your end user project.  Add a "logback.conf" file and a library dependency on your base project, and you're done.
lazy val guice = (project in file("guice")).
  settings(
    name := "terse-logback-guice",
    publish / skip := true,
    mainClass := Some("example.Main"),
    libraryDependencies += "net.mguenther.idem" % "idem-core" % "0.1.0",
    libraryDependencies += "com.google.inject" % "guice" % "4.2.2",
    // https://tavianator.com/cgit/sangria.git
    libraryDependencies += "com.tavianator.sangria" % "sangria-slf4j" % "1.3.1"
  ).dependsOn(classic).aggregate(classic)

