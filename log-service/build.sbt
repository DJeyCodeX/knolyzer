import Dependencies._


lazy val logAnalysis = (
  Project("akka-streams-example", file("."))
  settings(
    organization := "com.knoldus",
    name := "akka-streams-example",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.8",
    libraryDependencies ++= dependencies,
  )
)
