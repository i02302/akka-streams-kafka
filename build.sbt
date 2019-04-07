name := "hoge-akka-stream-kafka"

version := "0.1"

scalaVersion := "2.12.8"

organization := "com.i02302"

lazy val core = project

lazy val receiver = project.dependsOn(core)

lazy val launcher = project.dependsOn(core)
