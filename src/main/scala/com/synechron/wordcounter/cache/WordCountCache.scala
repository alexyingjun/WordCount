package com.synechron.wordcounter.cache

import com.synechron.wordcounter.runner.WordCounterConsumerRunner
import com.synechron.wordcounter.util.Translator
import com.synechron.wordcounter.validator.{Validator, WordValidator}
import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}

object WordCountCache extends LocalCache {
  private val wordCountMap: ConcurrentHashMap[String, ValueItem] = new ConcurrentHashMap[String, ValueItem]()
  private val validator: Validator = WordValidator
  private var translator: Translator = new Translator

  import java.util.concurrent.Executors
  import scala.concurrent._

  implicit val ec = new ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(20)

    def execute(runnable: Runnable) {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable) {}
  }

  for(_ <- 0 until 15) ec.execute(new WordCounterConsumerRunner)

  def init():Unit = println("Cache initialised")
  def setTranslator(translator: Translator):Unit = this.translator = translator

//  var token = new LinkedBlockingQueue[Boolean](1)
//  token.add(true)

  /**
   * Get count for a given word from the cache
   *
   * @param word
   * given word to retrieve the count
   * @return
   * count of the given word, return 0 if not presented
   */
  override def getCount(word: String): Long = {
    println("Get count for "+word)
    println("Current map => "+wordCountMap)
    if(wordCountMap.containsKey(word)) println("Current map has count for "+word)
    else println("Current map has no count for "+word)
    val count = wordCountMap.getOrDefault(word, ValueItem(0)).value
    println("Got count "+count)
//    token.add(true)
    count
  }

  def getCountFuture(word: String) = Future[Long] {
    println("Work on future")
//    token.take()
    getCount(word)
  }

  def validateRawWord(word: String): Boolean = {
    try {
      val tWord = translator.translate(word)
      println("Translated "+word+" to "+tWord)
      val valid = validator.validate(tWord)
      println("Validate result is "+valid)
      valid
    } catch {
      case _: Throwable => println("Error in add while translating word " + word)
        false
    }
  }

  private def addToMap(entry: (String, Long)) = {
    val tWord = translator.translate(entry._1)
    if (!tWord.equalsIgnoreCase(entry._1)) {  //not a english word, not exist in map
      val countItem = wordCountMap.getOrDefault(tWord.toLowerCase, ValueItem(0))
      wordCountMap.putIfAbsent(tWord.toLowerCase, countItem)
      wordCountMap.putIfAbsent(entry._1.toLowerCase,countItem)
    }
    wordCountMap.merge(tWord.toLowerCase, ValueItem(entry._2), (a, b) => {
      a.value += b.value
      a
    })
  }

  override def addToCache(map: Map[String, Long]): Unit = {
//    token.take()
    val groupedMap = map.groupBy(e => wordCountMap.containsKey(e._1))
    groupedMap.get(true) match {
      case Some(map) => {
        println("Add without translate ==> " + map)
        map.foreach(e => wordCountMap.merge(e._1, ValueItem(e._2), (a, b) => {
          a.value += b.value
          a
        }))
      }
      case None =>
    }
    groupedMap.get(false) match {
      case Some(map) => {
        println("Add with translate ==> " + map);
        map.filter(x => validateRawWord(x._1))
          .foreach(addToMap(_))
      }
      case None =>
    }
//    token.add(true)
  }

  override def getMap(): ConcurrentHashMap[String, ValueItem] = wordCountMap
}
