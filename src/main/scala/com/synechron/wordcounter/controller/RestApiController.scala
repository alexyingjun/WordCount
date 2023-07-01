package com.synechron.wordcounter.controller

import cask._
import com.synechron.wordcounter.core.{WordCounterImpl}
import com.synechron.wordcounter.core.flow.WordCounterFlow


object RestApiController extends MainRoutes{
  private val processFlow = new WordCounterFlow()
  private val wordCounter = WordCounterImpl(processFlow)

  @get("/")
  def healthCheck() = "Welcome to word counter service"

  @get("/count/:word")
  def getWordCount(word: String): Long = wordCounter.get(word)

  @postJson("/add")
  def addWords(words: Seq[String]): Unit = wordCounter.add(words: _*)

  initialize()
}
