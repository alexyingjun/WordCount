package com.synechron.wordcounter.controller

import cask._
import com.synechron.wordcounter.cache.WordCountCache
import com.synechron.wordcounter.core.WordCounterImpl


object RestApiController extends MainRoutes{

  private val wordCounter = new WordCounterImpl

  @get("/")
  def healthCheck() = "Welcome to word counter service"

  @get("/count/:word")
  def getWordCount(word: String): Long = wordCounter.getCountOfWord(word)

  @postJson("/add")
  def addWords(words: Seq[String]): Unit = wordCounter.add(words: _*)

  initialize()
  WordCountCache.init()
}
