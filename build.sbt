lazy val scala211Version = "2.11.12"
lazy val scala212Version = "2.12.10"
lazy val sparkVersion = "3.1.1"



lazy val commonSettings = Seq(
  name := "spark-sftp",
  organization := "com.springml",
  version := "1.2.0",
  scalaVersion := scala212Version,
  crossScalaVersions := Seq(scala211Version, scala212Version)
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  val javaVersion = sys.props("java.specification.version")
  if (javaVersion != "1.8")
    sys.error("Java 1.8 is required but " + javaVersion + " found instead")
}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

lazy val shaded = (project in file("."))
  .settings(commonSettings)

// Test
lazy val commonTestDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "test"
)

libraryDependencies ++= (commonTestDependencies ++ Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "com.springml" % "sftp.client" % "1.0.3",
  "org.mockito" % "mockito-core" % "2.0.31-beta",
  "com.databricks" %% "spark-xml" % "0.5.0"
))


// Repositories
resolvers ++= Seq(
  "Spark Package Main Repo" at "https://dl.bintray.com/spark-packages/maven",
  "Artifactory" at "https://fullcontact.jfrog.io/fullcontact"
)

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

publishTo := {
  val repoUrl = "https://fullcontact.jfrog.io/fullcontact"
  if (version.value.endsWith("SNAPSHOT"))
    Some("Artifactory Realm" at repoUrl + "/libs-snapshots-local;build.timestamp=" + new java.util.Date().getTime)
  else
    Some("Artifactory Realm"  at repoUrl + "/libs-releases-local")
}

pomExtra := (
  <url>https://github.com/springml/spark-sftp</url>
    <licenses>
      <license>
        <name>Apache License, Verision 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/springml/spark-sftp</connection>
      <developerConnection>scm:git:git@github.com:springml/spark-sftp</developerConnection>
      <url>github.com/springml/spark-sftp</url>
    </scm>
    <developers>
      <developer>
        <id>springml</id>
        <name>Springml</name>
        <url>http://www.springml.com</url>
      </developer>
    </developers>)
