package com.synechron.wordcounter.cache

import com.synechron.wordcounter.cache.WordCountCache.getCountFuture
import com.synechron.wordcounter.mode.{RequestItem, RequestType}

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.{Duration, FiniteDuration}

object WordBuffer {
  val queue = new LinkedBlockingQueue[RequestItem]

  def dequeue = {
    println("Dequeue request from "+queue)
    val item = queue.take()
    println("Dequeue completed left "+queue)
    println("Dequeued item "+item)
    item
  }

  def enqueueAddWords : Any => Long = input => {
    println("Enquque words "+input)
    queue add RequestItem(RequestType.ADD, input.asInstanceOf[Map[String, Long]], input)
    println("Enquque complete ====> "+queue)
    1
  }

  def enqueueGetWordCount : Any => Long = input => {
//    val wordCount = getCountFuture(input.asInstanceOf[Seq[String]].head)
    val wordCount = input.asInstanceOf[Future[Long]]
    queue add RequestItem(RequestType.GET, wordCount, "")
//    val maxWaitTime: FiniteDuration = Duration(20, TimeUnit.SECONDS)
    println("Added GET request to queue")
    0
//    Await.result(wordCount, maxWaitTime)
//    var count = 0L
//    wordCount.onComplete{
//      case Success(c) => {
//        println("Future returned result "+c)
//        count = c
//        count
//      }
//      case Failure(exception) => exception
//    }
//    println("Got count: "+count+" from future")
//    count
  }

  def clearBuffer : Unit = {
    println("Before clear: "+queue)
    queue.clear()
  }
}