name := "squant"

version := "1.0"

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
  "com.github.philcali" %% "cronish" % "0.1.3",
  "com.github.scopt" % "scopt_2.11" % "3.5.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "com.google.code.gson" % "gson" % "2.8.0"
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last == "application.conf" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

//mainClass in assembly := Some("com.squant.cheetah.datasource.Updater")