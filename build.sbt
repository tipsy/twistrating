import play.PlayJava

name := """twistrating"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "com.google.inject" % "guice" % "3.0",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "org.axonframework" % "axon-core" % "2.3.1",
  "org.axonframework" % "axon-test" % "2.3.1",
  "com.firebase" % "firebase-client" % "1.0.17"
)
