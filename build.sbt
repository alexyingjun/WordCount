ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

libraryDependencies ++= List(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
  "com.lihaoyi" %% "cask" % "0.9.1",
  "org.scalatest" %% "scalatest" % "3.2.16" % "test",
  "org.scalactic" %% "scalactic" % "3.2.16" % "test",
  "com.lihaoyi" %% "requests" % "0.8.0" % "test",
  "org.mockito" %% "mockito-scala" % "1.16.42" % "test"
)

lazy val root = (project in file("."))
  .settings(
    name := "WordCount"
  )
