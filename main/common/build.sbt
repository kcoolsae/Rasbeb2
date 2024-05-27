name := "Rasbeb2 - common"

normalizedName := "common"

scalaVersion:= "2.13.12"

// minimal extract from commonAppSettings in main build file
libraryDependencies ++= Seq(
    "be.ugent.caagt" %% "play-utils" % "1.1",
)

