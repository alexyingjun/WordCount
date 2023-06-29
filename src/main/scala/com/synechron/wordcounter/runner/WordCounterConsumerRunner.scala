package com.synechron.wordcounter.runner

import com.synechron.wordcounter.cache.{WordBuffer, WordCountCache}
import com.synechron.wordcounter.mode.RequestType

import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}

class WordCounterConsumerRunner extends Runnable{

  override def run(): Unit = {
    val cache = WordCountCache
    val threadName = Thread.currentThread().getName
    println(threadName+" Consumer runner started ...")
    while(true){
      println(threadName+" Try to dequeue buffer ...")
      val value = WordBuffer.dequeue
      println(threadName + " Took Request type: "+value.requestType)
      value.requestType match{
        case RequestType.ADD =>
//          WordCountCache.isProcessing=true
          cache.addToCache(value.item.asInstanceOf[Map[String,Long]])
          println(threadName + " Map after add => "+cache.getMap)
        case RequestType.GET =>
          val wordCount = value.item.asInstanceOf[Future[Long]]
          println("Trying result")
          val maxWaitTime: FiniteDuration = Duration(20, TimeUnit.SECONDS)
          Await.result(wordCount, maxWaitTime)
          println(threadName+ " Future item processed")
      }
    }
  }
}
