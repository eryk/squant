name := "squant"

version := "1.0"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.14"
lazy val akkaHttpVersion = "10.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
  "com.typesafe.akka" % "akka-http_2.11" % akkaHttpVersion,
  "com.typesafe.akka" % "akka-http-spray-json_2.11" % akkaHttpVersion,
  "com.typesafe" % "config" % "1.3.1",
  "org.jsoup" % "jsoup" % "1.10.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true