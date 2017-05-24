
organization := "com.ctc"
name := "akka-jsend"
version := "0.1.0-SNAPSHOT"

crossScalaVersions := Seq("2.11.11", "2.12.2")
scalaVersion := crossScalaVersions.value.head

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",

  "-feature",
  "-unchecked",
  "-deprecation",

  "-language:postfixOps",
  "-language:implicitConversions",

  "-Ywarn-unused-import",
  "-Xfatal-warnings",
  "-Xlint:_"
)

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.6"
  val scalatestVersion = "3.0.3"

  Seq(
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test
  )
}

enablePlugins(ArtifactoryPublish)
