name := "Rasbeb2 - db"

normalizedName := "db"

libraryDependencies ++= Seq(
  "be.ugent.caagt" % "daohelper" % "1.1.12",
  "com.google.guava" % "guava" % "32.1.3-jre",  // = version used by play
  "org.apache.poi" % "poi-ooxml" % "5.2.2",

  "org.projectlombok" % "lombok" % "1.18.30" % Compile,

  "org.postgresql" % "postgresql" % "42.7.1" % Test,
  "junit" % "junit" % "4.13.2" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.assertj" % "assertj-core" % "3.24.2" % Test

)
