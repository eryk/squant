name := "squant"

version := "1.0"

scalaVersion := "2.12.0"

lazy val akkaVersion = "2.4.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true