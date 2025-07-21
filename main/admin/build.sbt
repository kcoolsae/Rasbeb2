name := "Rasbeb2 - admin"

normalizedName := "admin"

libraryDependencies ++= Seq(
  "org.playframework" %% "play-mailer" % "10.1.0",
  "org.playframework" %% "play-mailer-guice" % "10.1.0",

  "org.apache.poi" % "poi-ooxml" % "5.4.1"
)

PlayKeys.devSettings += "play.server.http.port" -> "9007"
