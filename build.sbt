// Settings file for all the modules.
import xml.Group
import sbt._
import Keys._

organization := "com.github.swagger-akka-http"

ThisBuild / scalaVersion := "2.13.6"

ThisBuild / crossScalaVersions := Seq("2.11.12", "2.12.14", "2.13.6", "3.0.1")

ThisBuild / organizationHomepage := Some(url("https://github.com/swagger-akka-http/swagger-scala-module"))

val scalaReleaseVersion = SettingKey[Int]("scalaReleaseVersion")
scalaReleaseVersion := {
  val v = scalaVersion.value
  CrossVersion.partialVersion(v).map(_._1.toInt).getOrElse {
    throw new RuntimeException(s"could not get Scala release version from $v")
  }
}

val scalaMajorVersion = SettingKey[Int]("scalaMajorVersion")
scalaMajorVersion := {
  val v = scalaVersion.value
  CrossVersion.partialVersion(v).map(_._2.toInt).getOrElse {
    throw new RuntimeException(s"could not get Scala major version from $v")
  }
}

ThisBuild / scalacOptions ++= {
  val additionalSettings =
    if (scalaReleaseVersion.value == 2 && scalaMajorVersion.value <= 12) {
      Seq("-language:existentials")
    } else {
      Seq.empty[String]
    }
  Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature") ++ additionalSettings
}

publishMavenStyle := true

Test / publishArtifact := false

pomIncludeRepository := { x => false }

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.32",
  "io.swagger.core.v3" % "swagger-core" % "2.1.10",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.32" % Test
)

publishTo := {
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
  else
    Some("Sonatype Nexus Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
}

ThisBuild / credentials += Credentials (Path.userHome / ".ivy2" / ".credentials")

ThisBuild / resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots")
)

homepage := Some(new URL("https://github.com/swagger-akka-http/swagger-scala-module"))

Test / parallelExecution := false

startYear := Some(2014)

licenses := Seq(("Apache License 2.0", new URL("http://www.apache.org/licenses/LICENSE-2.0.html")))

releasePublishArtifactsAction := PgpKeys.publishSigned.value

pomExtra := {
  pomExtra.value ++ Group(
    <scm>
      <connection>scm:git:git@github.com:swagger-akka-http/swagger-scala-module.git</connection>
      <developerConnection>scm:git:git@github.com:swagger-akka-http/swagger-scala-module.git</developerConnection>
      <url>https://github.com/swagger-akka-http/swagger-scala-module</url>
    </scm>
      <issueManagement>
        <system>github</system>
        <url>https://github.com/swagger-api/swagger-scala-module/issues</url>
      </issueManagement>
      <developers>
        <developer>
          <id>fehguy</id>
          <name>Tony Tam</name>
          <email>fehguy@gmail.com</email>
        </developer>
        <developer>
          <id>pjfanning</id>
          <name>PJ Fanning</name>
          <url>https://github.com/pjfanning</url>
        </developer>
      </developers>
  )
}

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("develop")),
  RefPredicate.Equals(Ref.Branch("1.5")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)
