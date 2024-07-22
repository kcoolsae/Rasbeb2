
name := "Rasbeb2 - parent project"

scalaVersion:= "2.13.12"

// subprojects
//////////////

lazy val root = (project in file(".")).aggregate(
  db, contest, admin, webjar
).settings(
  publish / skip := true
)

lazy val db = project in file("db")

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
ThisBuild / scalacOptions += "-release:21"
ThisBuild / javacOptions ++= Seq("-source", "21", "-target", "21")

// no documentation or sources in packaged jars
ThisBuild / packageDoc / publishArtifact := false
ThisBuild / packageSrc / publishArtifact := false

// settings common to both Play apps
lazy val commonAppSettings = Seq(

  scalaVersion:= "2.13.12",

  //
  libraryDependencies ++= Seq(
    javaCore, guice, javaJdbc,

    "be.ugent.caagt" %% "play-utils" % "1.1",
    "org.webjars" % "font-awesome" % "6.4.0",

    "org.projectlombok" % "lombok" % "1.18.34" % Compile,

    "org.postgresql" % "postgresql" % "42.7.3" % Runtime
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
)

