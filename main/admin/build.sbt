name := "Rasbeb2 - admin"

normalizedName := "admin"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "9.0.0",
  "com.typesafe.play" %% "play-mailer-guice" % "9.0.0",

  "org.apache.poi" % "poi-ooxml" % "5.3.0"
)

PlayKeys.devSettings += "play.server.http.port" -> "9007"
