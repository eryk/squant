name := "squant"

version := "1.0"

scalaVersion := "2.12.0"

lazy val akkaVersion = "2.4.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe" % "config" % "1.3.1",
  "org.jsoup" % "jsoup" % "1.10.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true