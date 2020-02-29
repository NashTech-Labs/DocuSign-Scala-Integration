
name := "DocuSign-Scala-Integration"

version := "1.0"

scalaVersion := "2.12.8"
 
libraryDependencies ++= Seq("com.docusign" % "docusign-esign-java" % "2.9.0", "com.sun.jersey" % "jersey-core" % "1.19.4",
"ch.qos.logback" % "logback-classic" %"1.2.3", "com.typesafe" % "config" % "1.2.1")
