package com.synechron.wordcounter.core

import com.synechron.wordcounter.aggregator.WordsAggregator
import com.synechron.wordcounter.cache.WordCountCache.getCountFuture
import com.synechron.wordcounter.cache.{LocalCache, ValueItem, WordCountCache}
import com.synechron.wordcounter.runner.{GetWordRequestProducerRunner, Getter, Producer, WordCounterProducerRunner}
import com.synechron.wordcounter.validator.{Validator, WordValidator}

import java.util.concurrent.{ConcurrentHashMap, TimeUnit}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}
import com.synechron.wordcounter.regulator.WordRegulator

abstract class WordCounter extends Producer[String] with Getter[Future[Long]]{

  protected def validator: Validator
  protected def localCache: LocalCache

  val aggregator = new WordsAggregator
  val regulator = WordRegulator(WordValidator)
  val inputFlow = regulator.filter _ andThen aggregator.aggregate andThen WordCountCache.addToCache
  override def get(item: Future[Long]): Long = GetWordRequestProducerRunner.get(item)
  /**
   * get count of given word
   * @param word
   *    the word to find the count for
   * @return
   *    how many times of the word appeared
   */
  def getCountOfWord(word: String): Long = {
    println("Request to get count for "+word)
    if(!validator.validate(word)) {
      println("Word "+word+" is invalid, abort")
      return 0
    }
    val wordCount = getCountFuture(word.toLowerCase)
    get(wordCount)
//    val count = GetWordRequestProducerRunner.add(word.toLowerCase)
//    wordCount.onComplete {
//      case Success(c) => {
//        println("Future returned result " + c)
//        return c
//      }
//      case Failure(exception) => exception
//        return 0
//    }
//    localCache.getCount(translatedWord)
    val maxWaitTime: FiniteDuration = Duration(20, TimeUnit.SECONDS)
    Await.result(wordCount, maxWaitTime)
  }

  def getMap(): ConcurrentHashMap[String, ValueItem] = localCache.getMap()

  override def add(words: String*): Unit = {
    println("Add words: "+words)
    WordCounterProducerRunner.add(words:_*)
  }

  def directAdd(words: String*): Unit = {
    println("Direct add words: " + words)
    inputFlow(words)
  }

  def directGet(word: String): Long = {
    println("Direct get word: " + word)
    WordCountCache.getCount(word)
  }
}
