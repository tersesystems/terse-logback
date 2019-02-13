import Dependencies._

// SBT is not what people are used to for Java projects, but it's what I know.
//
// "brew install sbt" to install sbt.
//
// "sbt run" to run the example.
//

// Code to censor sensitive content.
lazy val censor = (project in file("censor")).
  settings(
    libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "com.typesafe" % "config" % "1.3.3"
  )

// A "classic" project with the underlying appenders and infrastructure.
// Create your own github repository for something like this, and publish it to your artifactory or locally.
// THIS IS NOT INTENDED TO BE A DROP IN REPLACEMENT, but an example of structured logging with Logback.
lazy val classic = (project in file("classic")).
  settings(
    libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "org.fusesource.jansi" % "jansi" % "1.17.1", // color on windows
    libraryDependencies += "com.typesafe" % "config" % "1.3.3",
    libraryDependencies += "org.slf4j" % "jul-to-slf4j" % "1.7.25",
    libraryDependencies += "org.codehaus.janino" % "janino" % "3.0.11",
    libraryDependencies += "com.github.ben-manes.caffeine" % "caffeine" % "2.6.2"
).dependsOn(censor)

lazy val bytebuddy = (project in file("bytebuddy")).
  settings(
    libraryDependencies += "net.bytebuddy" % "byte-buddy" % "1.9.9",
    libraryDependencies += "net.bytebuddy" % "byte-buddy-agent" % "1.9.9"
  ).dependsOn(classic)

// Code to proxy and conditional logging.
lazy val proxy = (project in file("proxy")).dependsOn(classic)

// Code to manage context
lazy val context = (project in file("context")).dependsOn(classic)

// Your end user project.  Add a "logback.conf" file and a library dependency on your base project, and you're done.
lazy val example = (project in file("example")).
  settings(
    publish / skip := true,
    mainClass := Some("example.Main"),
    libraryDependencies += "net.mguenther.idem" % "idem-core" % "0.1.0"
  ).dependsOn(classic, proxy, context, bytebuddy)

// Your end user project.  Add a "logback.conf" file and a library dependency on your base project, and you're done.
lazy val guice = (project in file("guice")).
  settings(
    publish / skip := true,
    mainClass := Some("example.Main"),
    libraryDependencies += "net.mguenther.idem" % "idem-core" % "0.1.0",
    libraryDependencies += "com.google.inject" % "guice" % "4.2.2",
    // https://tavianator.com/cgit/sangria.git
    libraryDependencies += "com.tavianator.sangria" % "sangria-slf4j" % "1.3.1"
  ).dependsOn(classic, proxy, context)


lazy val root = (project in file(".")).enablePlugins(SbtTwirl)
  .settings(
    inThisBuild(List(
      organization := "com.tersesystems",
      crossPaths := false,
      autoScalaLibrary := false,
      version      := "0.1.0-SNAPSHOT",
      javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8"),
      testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
      libraryDependencies += "org.assertj" % "assertj-core" % "3.11.1" % Test,
      libraryDependencies += junitInterface % Test,
    )),
    // "sbt generateSources" will create source code for implementing the SLF4J logger API.
    TaskKey[Unit]("generateSources") := {
      // https://www.playframework.com/documentation/2.7.x/ScalaCustomTemplateFormat
      TwirlKeys.templateFormats += ("java" -> "play.twirl.api.TxtFormat.instance")

      val outdir = target.value / "sources"
      val classpath = (fullClasspath in Compile).value
      val scalaRun = (runner in run).value
      val log = streams.value.log
      val baseDir = baseDirectory.value

      // Clear the output directory first
      IO.delete(outdir)

      // Find the templates
      val templates = (sources in (Compile, TwirlKeys.compileTemplates)).value pair Path.relativeTo((sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value)

      val templateClasses = templates.map {
        case(_, name) =>
          val splitted = name.split('/')
          val fileName = splitted.last
          val Array(clazz, _, t) = fileName.split('.')
          (splitted.dropRight(1) ++ Seq(t, clazz)).mkString(".")
      }

      scalaRun.run("SourcesGenerator", Attributed.data(classpath), Seq(outdir.getAbsolutePath) ++ templateClasses, log).failed foreach (sys error _.getMessage)
    },
    name := "terse-logback-root",
    publish / skip := true,
    mainClass in Compile := (mainClass in Compile in example).value
  ).aggregate(censor, proxy, context, classic, example, guice, bytebuddy
).dependsOn(example) // dependsOn for the mainClass
