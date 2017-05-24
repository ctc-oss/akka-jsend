import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

import scala.language.postfixOps

object NoPublish extends AutoPlugin {
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )
}

object ArtifactoryPublish extends AutoPlugin {
  val host = sys.env.getOrElse("ARTIFACTORY_DEPLOY_HOST", "ARTIFACTORY_DEPLOY_HOST")
  val user = sys.env.getOrElse("ARTIFACTORY_DEPLOY_USER", "ARTIFACTORY_DEPLOY_USER")
  val pass = sys.env.getOrElse("ARTIFACTORY_DEPLOY_TOKEN", "ARTIFACTORY_DEPLOY_TOKEN")

  override def projectSettings = Seq(
    credentials += Credentials("Artifactory Realm", host, user, pass),
    publishTo := {
      val targetRepo = if (isSnapshot.value) "snapshot" else "release"
      Some("Artifactory Realm" at s"https://$host/libs-$targetRepo-local")
    },
    publishArtifact := true,
    publishMavenStyle := true
  )
}
