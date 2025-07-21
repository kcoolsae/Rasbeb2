// Comment out to get more information during initialization
logLevel := Level.Warn

// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.8")
//resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// See https://github.com/maichler/sbt-jupiter-interface
// resolvers += Resolver.jcenterRepo
addSbtPlugin("net.aichler" % "sbt-jupiter-interface" % "0.11.1")
