
name := "Rasbeb2 - parent project"

// subprojects
//////////////

lazy val root = (project in file(".")).aggregate(
  db, contest, admin, webjar
).settings(
  publish / skip := true
)

lazy val db = (project in file("db"))
  .settings(lombokSettings)

lazy val common = (project in file("common"))
  .dependsOn(db)
  .enablePlugins(PlayJava)

lazy val contest = (project in file("contest"))
  .dependsOn(db, common, webjar)
  .enablePlugins(PlayJava)
  .settings(commonAppSettings)

lazy val admin = (project in file("admin"))
  .dependsOn(db, common, webjar)
  .enablePlugins(PlayJava)
  .settings(commonAppSettings)

lazy val webjar = project in file("webjar")

// settings
///////////

Global / logLevel := Level.Warn

ThisBuild / version := "1.0-SNAPSHOT"
ThisBuild / organization := "be.ugent.rasbeb2"
ThisBuild / crossPaths := false
ThisBuild / autoScalaLibrary := false
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / scalacOptions += "-release:25"
ThisBuild / javacOptions ++= Seq("-source", "25", "-target", "25")

// no documentation or sources in packaged jars
ThisBuild / packageDoc / publishArtifact := false
ThisBuild / packageSrc / publishArtifact := false

// Below is needed to use Lombok for Java >= 23
lazy val lombokSettings = Seq(

  libraryDependencies ++= Seq(
    "org.projectlombok" % "lombok" % "1.18.46" % Provided
  ),

  Compile / javacOptions ++= {
    // Filters the compile classpath to find the lombok jar file
    val lombokJar = (Compile / dependencyClasspath).value
      .map(_.data)
      .find(_.getName.contains("lombok"))

    lombokJar match {
      case Some(jar) => Seq("-processorpath", jar.getAbsolutePath,
        "-processor",
        "lombok.launch.AnnotationProcessorHider$AnnotationProcessor")
      case None => Seq.empty
    }
  }
)

// settings common to both Play apps
lazy val commonAppSettings = Seq(

  ThisBuild / scalaVersion:= "2.13.18",

  //
  libraryDependencies ++= Seq(
    javaCore, guice, javaJdbc,

    "be.ugent.caagt" %% "play-utils" % "1.1",
    "org.webjars" % "font-awesome" % "6.7.2",

    "org.postgresql" % "postgresql" % "42.7.7" % Runtime
  ),

  // Allow routes to be in a different directory
  Compile / unmanagedResourceDirectories += baseDirectory.value / "routes",

  // Include local template extensions
  TwirlKeys.templateImports ++= Seq(
    "views.html.ext._",
    "views.html.b5._"
  ),

  // Include template extensions for be.ugent.caagt:play-utils
  TwirlKeys.templateImports ++= Seq(
    "_root_.be.ugent.caagt.play.util.TemplateJavaMagic._",
    "_root_.common._",
    "views.html.be.ugent.caagt.play.ext._"
  ),

  // Add imports to play util binders for routes
  play.sbt.routes.RoutesKeys.routesImport ++= Seq(
    "_root_.be.ugent.caagt.play.binders._"
  ),

  // do not generate javadoc
  Compile / doc / sources := Seq.empty,
  Compile / doc / scalacOptions += "-no-java-comments"
) ++ lombokSettings
