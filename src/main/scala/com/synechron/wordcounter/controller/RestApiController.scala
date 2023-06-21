package com.synechron.wordcounter.controller

import cask._
import com.synechron.wordcounter.core.WordCounterImpl
import com.synechron.wordcounter.util.Translator


object RestApiController extends MainRoutes{

  private val wordCounter = new WordCounterImpl(new Translator)

  @get("/")
  def healthCheck() = "Welcome to word counter service"

  @get("/count/:word")
  def getWordCount(word: String): Long = wordCounter.getCountOfWord(word)

  @postJson("/add")
  def addWords(words: Seq[String]): Unit = wordCounter.addWords(words: _*)

  initialize()
}
