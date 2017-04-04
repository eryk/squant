name := "squant"

version := "1.1"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.14"

resolvers += Resolver.bintrayRepo("fcomb", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
  "com.typesafe" % "config" % "1.3.1",
  "org.jsoup" % "jsoup" % "1.10.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.apache.commons" % "commons-math3" % "3.6.1",
  "com.tictactec" % "ta-lib" % "0.4.0",
  "com.quantifind" % "wisp_2.11" % "0.0.4",
  "org.yaml" % "snakeyaml" % "1.17",
  //http://www.sauronsoftware.it/projects/cron4j/manual.php
  "it.sauronsoftware.cron4j" % "cron4j" % "2.2.5",

  "com.github.scopt" % "scopt_2.11" % "3.5.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "com.google.code.gson" % "gson" % "2.8.0",
  "org.apache.hbase" % "hbase-client" % "1.3.0",
  "org.apache.hbase" % "hbase-common" % "1.3.0",
  "org.apache.hadoop" % "hadoop-common" % "2.7.3" excludeAll ExclusionRule(organization = "javax.servlet"),
  "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test",
  "org.apache.poi" % "poi" % "3.15"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case PathList(ps@_*) if ps.last == "application.conf" => MergeStrategy.discard
  case x => MergeStrategy.first
}

//mainClass in assembly := Some("com.squant.cheetah.datasource.Updater")