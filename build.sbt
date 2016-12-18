name := "squant"

version := "1.0"

scalaVersion := "2.12.0"

lazy val akkaVersion = "2.4.13"
lazy val akkaHttpVersion = "10.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % akkaVersion,
  "com.typesafe.akka" % "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" % "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe" % "config" % "1.3.1",
  "org.jsoup" % "jsoup" % "1.10.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true