// Comment out to get more information during initialization
logLevel := Level.Warn

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0")
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// See https://github.com/maichler/sbt-jupiter-interface
// resolvers += Resolver.jcenterRepo
addSbtPlugin("net.aichler" % "sbt-jupiter-interface" % "0.11.1")
