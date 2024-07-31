// Comment out to get more information during initialization
logLevel := Level.Warn

// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.5")
//resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// See https://github.com/maichler/sbt-jupiter-interface
// resolvers += Resolver.jcenterRepo
addSbtPlugin("net.aichler" % "sbt-jupiter-interface" % "0.11.1")
