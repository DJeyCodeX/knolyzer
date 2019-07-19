package com.knoldus

import java.net.InetAddress

import org.joda.time.Instant

case class LogEntry(
  ip: InetAddress,
  time: Instant,
  req: String,
  userAgent: String
)


case class LogEntryNew(
                     ip: String,
                     time: String,
                     req: String,
                     userAgent: String
                   )
