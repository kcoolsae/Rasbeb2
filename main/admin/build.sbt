name := "Rasbeb2 - admin"

normalizedName := "admin"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "8.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",

  "org.apache.poi" % "poi-ooxml" % "5.2.2"
)

PlayKeys.devSettings += "play.server.http.port" -> "9007"
