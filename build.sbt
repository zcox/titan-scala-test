import AssemblyKeys._

organization := "com.pongr"

name := "titan-scala-test"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "com.thinkaurelius.titan" % "titan" % "0.1.0",
  "com.google.guava" % "guava" % "12.0" //if we don't specify this, it uses guava r08 which is missing the com.google.common.base.Optional class
)

assemblySettings //currently fails due to lots of duplicates in transitive dependencies

net.virtualvoid.sbt.graph.Plugin.graphSettings //gives us a nice dependency-tree task in sbt
