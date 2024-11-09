import scala.collection.Seq

ThisBuild / version := ""

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "triangles-min-path",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "2.2.0",
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:postfixOps",
      "-language:higherKinds")
  )

addCommandAlias(
  "build",
  """|;
     |clean;
     |assembly;
  """.stripMargin)
