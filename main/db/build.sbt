name := "Rasbeb2 - db"

normalizedName := "db"

libraryDependencies ++= Seq(
  "be.ugent.caagt" % "daohelper" % "1.1.13",
  "com.google.guava" % "guava" % "32.1.3-jre",  // = version used by play
  "org.apache.poi" % "poi-ooxml" % "5.3.0",

  "org.projectlombok" % "lombok" % "1.18.34" % Compile,
  "org.postgresql" % "postgresql" % "42.7.3" % Test,

  "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
  "org.assertj" % "assertj-core" % "3.26.3" % Test

)

Test / parallelExecution := false  // currently cannot clone database in parallel - all clones have the same name
Test / publishArtifact := false

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
