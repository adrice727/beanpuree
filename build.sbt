import ReleaseTransformations._

lazy val beanPuree = (project in file ("."))
  .settings(
    name := "beanpuree",
    scalaVersion := "2.12.4",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.4", "2.13.0-M2"),
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
    organization := "me.limansky",
    incOptions := incOptions.value.withLogRecompileOnMacro(false),
    libraryDependencies ++= Seq(
      "com.chuusai"         %% "shapeless"        % "2.3.2",
      "org.typelevel"       %% "macro-compat"     % "1.1.1",
      "org.scala-lang"      % "scala-reflect"     % scalaVersion.value    % Provided,
      "org.scala-lang"      % "scala-compiler"    % scalaVersion.value    % Provided,
      "org.scalatest"       %% "scalatest"        % "3.0.4"               % Test
    ) ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) => Seq(
          "org.scalamacros" %% "quasiquotes" % "2.1.0",
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.patch)
        )
        case Some((2, x)) if x >= 11 => Seq(
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch)
        )
        case _ => sys.error("Unsupported Scala version")
      }
    },
    publishSettings,
    releaseSettings
  )

lazy val publishSettings = Seq(
  licenses += ("Apache 2.0 License", url("http://www.apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("http://github.com/limansky/beanpuree")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/limansky/beanpuree"),
      "scm:git:https://github.com/limansky/beanpuree.git",
      Some("scm:git:git@github.com:limansky/beanpuree.git")
    )
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := <developers>
    <developer>
      <id>limansky</id>
      <name>Mike Limansky</name>
      <url>http://github.com/limansky</url>
    </developer>
  </developers>
)

lazy val releaseSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  )
)
