// Comment out to get more information during initialization
logLevel := Level.Warn

// The Play plugin
// addSbtPlugin("org.playframework" % "sbt-plugin" % "2.9.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0")
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
//resolvers += "Typesafe Simple Repository" at "https://repo.typesafe.com/typesafe/simple/maven-releases/"
// addDependencyTreePlugin
