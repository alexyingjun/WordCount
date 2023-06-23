package com.synechron.wordcounter.cache

import com.synechron.wordcounter.util.Translator
import com.synechron.wordcounter.validator.{Validator, WordValidator}

import java.util.concurrent.ConcurrentHashMap

object WordCountCache extends LocalCache {
  private val wordCountMap: ConcurrentHashMap[String, ValueItem] = new ConcurrentHashMap[String, ValueItem]()
  private val validator: Validator = WordValidator

  /**
   * Get count for a given word from the cache
   *
   * @param word
   * given word to retrieve the count
   * @return
   * count of the given word, return 0 if not presented
   */
  override def getCount(word: String): Long = wordCountMap.getOrDefault(word, ValueItem(0)).value

  def validateRawWord(word: String, translator: Translator): Boolean = {
    try {
      val tWord = translator.translate(word)
      validator.validate(tWord)
    } catch {
      case _: Throwable => println("Error in add while translating word " + word)
        false
    }
  }

  private def addToMap(entry: (String, Long), translator: Translator) = {
    val tWord = translator.translate(entry._1)
    if (!tWord.equalsIgnoreCase(entry._1)) {
      wordCountMap.putIfAbsent(entry._1.toLowerCase, wordCountMap.get(tWord))
    }
    wordCountMap.merge(tWord.toLowerCase, ValueItem(entry._2), (a, b) => {
      a.value += b.value
      a
    })
  }

  override def addToCache(map: Map[String, Long], translator: Translator): Unit = {
    val groupedMap = map.groupBy(e => wordCountMap.containsKey(e._1))
    groupedMap.get(true) match {
      case Some(map) => println("Add without translate ==> "+map);map.foreach(e=>wordCountMap.merge(e._1, ValueItem(e._2), (a,b)=> {
        a.value+=b.value
        a
      }))
      case None =>
    }
    groupedMap.get(false) match {
      case Some(map) => println("Add with translate ==> "+map);map.filter(x => validateRawWord(x._1, translator))
        .foreach(addToMap(_,translator))
      case None =>
    }
  }

  override def getMap(): ConcurrentHashMap[String, ValueItem] = wordCountMap
}
