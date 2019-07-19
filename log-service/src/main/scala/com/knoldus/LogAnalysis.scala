package com.knoldus

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import scopt.{ OptionParser}

object LogAnalysis extends App {

  object Commands {
    sealed trait Command
    case object Generate extends Command
    case object Analyze extends Command
  }

  import Commands._

  case class LogConfig(cmd: Command = null, file: File = null, configFile: File = null)

  val parser: OptionParser[LogConfig] = new OptionParser[LogConfig]("log-analysis.sh") {
    head("log-analysis", "1.0.0-SNAPSHOT")

    cmd("generate").action((_, c) => c.copy(cmd = Generate)).
      text("Generate log file.")

    cmd("analyze").action((_, c) => c.copy(cmd = Analyze)).
      text("Analyze log file.").children(
      arg[File]("<file>") required() action { (v, c) =>
        c.copy(file = v)
      } validate { f =>
        if (f.isFile) success else failure("File does not exist")
      } text ("Path to log file")
    )

    arg[File]("<file>")
      .required()
      .action { (v, c) => c.copy(configFile = v)}
      .validate { f => if (f.isFile) success else failure("File does note exist") }
      .text("configFile is required")
  }

  for {
    config <- parser.parse(args, LogConfig())
  } yield {
    config.cmd match {
      case Generate => LogGenerator()
      case Analyze => {
        val logConfig: Config = ConfigFactory.parseFile(config.configFile)
        LogAnalyzer(config.file.getAbsolutePath, logConfig)
      }

    }
  }

}
