import play.Project._

name := "dmc-delorean"

version := "1.0"

playScalaSettings

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "org.mockito" % "mockito-all" % "1.9.5",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1"
)