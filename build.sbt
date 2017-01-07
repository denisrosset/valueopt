import sbtbuildinfo.Plugin._
import sbtunidoc.{Plugin => UnidocPlugin}
import sbtunidoc.Plugin.UnidocKeys._
import pl.project13.scala.sbt.SbtJmh
import ReleaseTransformations._

lazy val scalaTestVersion = "3.0.0"

// Projects

lazy val valueopt = project.in(file("."))
  .settings(moduleName := "valueopt-root")
  .settings(optSettings)
  .settings(unidocSettings)
  .settings(noPublishSettings)
  .aggregate(valueoptJVM, valueoptJS)
  .dependsOn(valueoptJVM, valueoptJS)

lazy val valueoptJVM = project.in(file(".optJVM"))
  .settings(moduleName := "valueopt-aggregate")
  .settings(optSettings)
  .settings(unidocSettings)
  .settings(noPublishSettings)
  .aggregate(coreJVM, testsJVM)
  .dependsOn(coreJVM, testsJVM)

lazy val valueoptJS = project.in(file(".optJS"))
  .settings(moduleName := "valueopt-aggregate")
  .settings(optSettings)
  .settings(unidocSettings)
  .settings(noPublishSettings)
  .aggregate(coreJS, testsJS)
  .dependsOn( coreJS, testsJS)
  .enablePlugins(ScalaJSPlugin)

lazy val core = crossProject.crossType(CrossType.Pure)
  .settings(moduleName := "valueopt-core")
  .settings(optSettings:_*)
  .settings(coreSettings:_*)
  .settings(buildInfoSettings:_*)
  .settings(crossVersionSharedSources:_*)
  .jvmSettings(commonJvmSettings:_*)
  .jsSettings(commonJsSettings:_*)

lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val tests = crossProject.crossType(CrossType.Pure)
  .settings(moduleName := "valueopt-tests")
  .settings(optSettings:_*)
  .settings(scalaTestSettings:_*)
  .settings(noPublishSettings:_*)
  .jvmSettings(commonJvmSettings:_*)
  .jsSettings(commonJsSettings:_*)
  .dependsOn(core)

lazy val testsJVM = tests.jvm
lazy val testsJS = tests.js

// General settings

addCommandAlias("validateJVM", ";coreJVM/scalastyle;coreJVM/test;testsJVM/test")

addCommandAlias("validateJS", ";coreJS/test;testsJS/test")

addCommandAlias("validate", ";validateJVM;validateJS")

lazy val buildSettings = Seq(
  organization := "org.spire-math",
  scalaVersion := "2.12.1",
  crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.1")
)

lazy val commonSettings = Seq(
  scalacOptions ++= commonScalacOptions.diff(Seq(
    "-language:existentials",
    "-Xlint",
    "-Ywarn-value-discard"
  )),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots")
  )
) ++ scalaMacroDependencies ++ warnUnusedImport

lazy val commonJsSettings = Seq(
  scalaJSStage in Global := FastOptStage,
  parallelExecution in Test := false
)

lazy val commonJvmSettings = Seq(
  // -optimize has no effect in scala-js other than slowing down the build
  scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor <= 11 => Seq("-optimize") // remove 2.12 ?
    case _ => Seq.empty
  }),
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")
)

lazy val publishSettings = Seq(
  homepage := Some(url("http://spire-math.org")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  pomExtra := (
    <scm>
      <url>git@github.com:non/opt.git</url>
      <connection>scm:git:git@github.com:non/opt.git</connection>
    </scm>
    <developers>
      <developer>
        <id>d_m</id>
        <name>Erik Osheim</name>
        <url>http://github.com/non/</url>
      </developer>
      <developer>
        <id>denisrosset</id>
        <name>Denis Rosset</name>
        <url>http://github.com/denisrosset/</url>
      </developer>
      <developer>
        <id>tixxit</id>
        <name>Tom Switzer</name>
        <url>http://github.com/tixxit/</url>
      </developer>
    </developers>
  )
) ++ credentialSettings ++ sharedPublishSettings ++ sharedReleaseProcess

lazy val scoverageSettings = Seq(
  coverageMinimum := 40,
  coverageFailOnMinimum := false,
  coverageHighlighting := scalaBinaryVersion.value != "2.10"
)

// Project's settings

lazy val coreSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](version, scalaVersion),
  buildInfoPackage := "opt"
)

lazy val scalaCheckSettings  = Seq(libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.13.4" % "test")

lazy val scalaTestSettings = Seq(
  libraryDependencies += "org.scalatest" %%% "scalatest" % scalaTestVersion % "test"
)

lazy val optSettings = buildSettings ++ commonSettings ++ publishSettings ++ scoverageSettings

lazy val unidocSettings = UnidocPlugin.unidocSettings ++ Seq(
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(testsJVM)
)

////////////////////////////////////////////////////////////////////////////////////////////////////
// Base Build Settings - Should not need to edit below this line.
// These settings could also come from another file or a plugin.
// The only issue if coming from a plugin is that the Macro lib versions
// are hard coded, so an overided facility would be required.

addCommandAlias("gitSnapshots", ";set version in ThisBuild := git.gitDescribedVersion.value.get + \"-SNAPSHOT\"")

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val crossVersionSharedSources: Seq[Setting[_]] =
  Seq(Compile, Test).map { sc =>
    (unmanagedSourceDirectories in sc) ++= {
      (unmanagedSourceDirectories in sc ).value.map {
        dir:File =>
          CrossVersion.partialVersion(scalaBinaryVersion.value) match {
            case Some((major, minor)) =>
              new File(s"${dir.getPath}_$major.$minor")
            case None =>
              sys.error("couldn't parse scalaBinaryVersion ${scalaBinaryVersion.value}")
          }
      }
    }
  }

lazy val scalaMacroDependencies: Seq[Setting[_]] = Seq(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
      case Some((2, scalaMajor)) if scalaMajor >= 11 => Seq()
      // in Scala 2.10, quasiquotes are provided by macro paradise
      case Some((2, 10)) =>
        Seq(
          compilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full),
              "org.scalamacros" %% "quasiquotes" % "2.0.1" cross CrossVersion.binary
        )
    }
  }
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

lazy val sharedPublishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  }
)
 
lazy val sharedReleaseProcess = Seq(
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
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges)
)

lazy val warnUnusedImport = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        Seq()
      case Some((2, n)) if n >= 11 =>
        Seq("-Ywarn-unused-import")
    }
  },
  scalacOptions in (Compile, console) ~= {_.filterNot("-Ywarn-unused-import" == _)},
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
)

// For Travis CI - see http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci
lazy val credentialSettings = Seq(
  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)
