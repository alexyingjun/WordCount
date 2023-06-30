package com.synechron.wordcounter.controller

import cask._
import com.synechron.wordcounter.core.{WordCounterImpl}
import com.synechron.wordcounter.core.flow.WordCounterFlow


object RestApiController extends MainRoutes{
  val processFlow = new WordCounterFlow()
  private val wordCounter = new WordCounterImpl(processFlow)

  @get("/")
  def healthCheck() = "Welcome to word counter service"

  @get("/count/:word")
  def getWordCount(word: String): Long = wordCounter.get(word)

  @postJson("/add")
  def addWords(words: Seq[String]): Unit = wordCounter.add(words: _*)

  initialize()
}
