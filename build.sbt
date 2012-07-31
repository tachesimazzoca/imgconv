name := "imgconv"

version := "1.0"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
    "javax.servlet" % "servlet-api" % "2.5",
    "org.apache.httpcomponents" % "httpcore" % "4.1.2",
    "org.apache.httpcomponents" % "httpclient" % "4.1.2"
)

artifactName := { (config:String, module:ModuleID, artifact:Artifact) =>
  artifact.name + "." + artifact.extension
}
