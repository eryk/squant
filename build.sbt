name := "squant"

version := "1.0"

scalaVersion := "2.12.0"

lazy val akkaVersion = "2.4.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "junit" % "junit" % "4.12" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true