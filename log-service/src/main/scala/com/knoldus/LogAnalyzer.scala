package com.knoldus

import _root_.akka.actor.ActorSystem
import _root_.akka.stream.ActorMaterializer
import com.knoldus.akka.io.csv.CsvReader
import com.knoldus.akka.io.file.FileReader
import _root_.akka.stream.scaladsl.Sink
import com.typesafe.config.Config

object LogAnalyzer {

  implicit val system = ActorSystem("log-analyzer")
  implicit val materializer = ActorMaterializer()

  import com.knoldus.akka.io.csv.Parsers._
  import LogParsers._

  lazy val csv = new CsvReader[LogEntry]

  def duplicate(e1: LogEntry, e2:  LogEntry): Boolean = {
    val interval = e2.time.getMillis - e1.time.getMillis
    interval >= 0 && interval < 1000 && e1.req == e2.req
  }

  type ScanResult = (Seq[LogEntry], Option[Seq[LogEntry]])

  def duplicates(prev: ScanResult, next: LogEntry): ScanResult = {
    val (acc, result) = prev
    acc match {
      case l :+ last if duplicate(last, next) => (acc :+ next, None)
      case l :+ last => (Seq(next), if (acc.size > 1) Some(acc) else None)
      case Nil => (Seq(next), None)
    }
  }

  def output(e: LogEntry): String = s"${e.ip.getHostAddress} - ${e.time} - ${e.req}"

  def output(entries: Seq[LogEntry]): String =
    (" " ++ entries.map(output)).mkString("\n")


  /*
  def apply(path: String): Unit = {
    csv.read(FileReader.readContinuously(path, "UTF-8")).
      groupBy(Int.MaxValue, entry => (entry.ip, entry.userAgent)).
      scan[ScanResult]((Seq(), None))(duplicates).
      collect { case (_, Some(entries)) => entries }.
      mergeSubstreams.
      runWith(Sink.foreach(println))
  }*/


  def apply(path: String, logConfig : Config): Unit = {
    val appLabel = logConfig.getString("conf.name")
    System.out.println(" Name of the Log App " + logConfig.getString("conf.name") )
    System.out.println(" title of the Log App " + logConfig.getString("conf.title") )
    System.out.println(" URL of the Log " + logConfig.getString("conf.url") )
    FileReader.readContinuously(path, "UTF-8").map(parseLog).runWith(Sink.foreach(LogProducer.publishLog("Test",_)))
  }

  val logRegex = """^(\S+ \d\d \d\d:\d\d:\d\d) (\S+) (\S+): (.*)""".r;


  // Parses log and returns case class of the returned values.
  def parseLog(line: String): LogEntryNew = {
    val logRegex(instant,ip,userAgent,reg) = line;
    println(s"$instant $ip $userAgent $reg")
    return LogEntryNew(ip,instant,reg,userAgent);
  }

  // Function to assert if a String has integer and if not return 0 by default.
  def assertInt(variable:String):Int = {
    if(variable.forall(_.isDigit)){
      return variable.toInt
    }else{
      return 0;
    }
  }




}
