package com.synechron.wordcounter.runner

import com.synechron.wordcounter.aggregator.WordsAggregator
import com.synechron.wordcounter.cache.WordBuffer
import com.synechron.wordcounter.regulator.WordRegulator
import com.synechron.wordcounter.validator.WordValidator

object WordCounterProducerRunner extends Producer[String]{
  val aggregator = new WordsAggregator
  val regulator = WordRegulator(WordValidator)
  val inputFlow = regulator.filter _ andThen aggregator.aggregate andThen WordBuffer.enqueueAddWords

  override def add(item: String*): Unit = inputFlow(item)
}
