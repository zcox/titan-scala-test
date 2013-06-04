import AssemblyKeys._

organization := "com.pongr"

name := "titan-scala-test"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "com.thinkaurelius.titan" % "titan-cassandra" % "0.3.1"
)

scalacOptions := Seq("-unchecked", "-deprecation")

assemblySettings //currently fails due to lots of duplicates in transitive dependencies

net.virtualvoid.sbt.graph.Plugin.graphSettings //gives us a nice dependency-tree task in sbt
