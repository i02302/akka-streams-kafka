name := "core"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.12.8",
  "com.typesafe.akka" %% "akka-actor" % "2.5.22",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.22" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.22",
  "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.1",
  "ch.qos.logback" % "logback-classic" % "0.9.28",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.22",
  "org.json4s" %% "json4s-native" % "3.6.5"

)