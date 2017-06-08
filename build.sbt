lazy val root = (project in file("."))
  .settings(
    name := "specs2-elasticsearch",
    organization := "com.nathankleyn",
    scalaVersion := "2.11.1",
    // FIXME: Add Scala 2.12 support when we can migrate to a newer Elasticsearch library.
    // crossScalaVersions := Seq("2.11.11"),
    version := "0.1.0",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    libraryDependencies ++= Seq(
      "commons-io" % "commons-io" % "2.5",
      "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.5.17",
      "org.elasticsearch" % "elasticsearch" % "1.5.2",
      "org.specs2" %% "specs2-core" % "3.8.6"
    )
  )
