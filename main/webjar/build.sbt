name := "Rasbeb2 - webjar"

normalizedName := "webjar"

// remove most of play functionality - we only need to build a webjar
scalaVersion := "2.13.14"
enablePlugins(PlayJava)
disablePlugins(PlayFilters, PlayLogback, PlayPekkoHttpServer)

// run the bootstrap build script whenever a file in scss changes
lazy val customizeBootstrap = TaskKey[Unit]("customizeBootstrap", "Calls 'sass' to customize bootstrap")

customizeBootstrap := {
    import sys.process._
    Process ("./customize-bootstrap.sh", baseDirectory.value) !
}

// make other css files from scss
lazy val makeCSS = TaskKey[Unit]("makeCSS", "Calls 'sass' to make some css files")

makeCSS := {
    import sys.process._
    Process ("./make-css.sh", baseDirectory.value) !
}

Compile/compile := (Compile/compile).dependsOn(customizeBootstrap).value
Compile/compile := (Compile/compile).dependsOn(makeCSS).value

// remove dependencies on scala and play in published result:
// see https://stackoverflow.com/questions/27835740/sbt-exclude-certain-dependency-only-during-publish

import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
import scala.xml.transform.{RewriteRule, RuleTransformer}

pomPostProcess := { (node: XmlNode) =>
  new RuleTransformer(new RewriteRule {
    override def transform(node: XmlNode): XmlNodeSeq = node match {
      case e: Elem if e.label == "dependency"  =>
        Comment("Dependencies have been removed")
      case _ => node
    }
  }).transform(node).head
}


