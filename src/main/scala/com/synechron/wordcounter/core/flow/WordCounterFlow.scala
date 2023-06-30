package com.synechron.wordcounter.core.flow

import com.synechron.wordcounter.core.cache.WordCountCache
import com.synechron.wordcounter.core.flow.aggregator.WordsAggregator
import com.synechron.wordcounter.core.flow.regulator.WordRegulator
import com.synechron.wordcounter.core.flow.validator.{Validator, WordValidator}

case class WordCounterFlow(aggregator: WordsAggregator, regulator: WordRegulator) {

  def this() {
    this(new WordsAggregator, WordRegulator(new WordValidator))
  }

  def this(aggregator: WordsAggregator) {
    this(aggregator, WordRegulator(new WordValidator))
  }

  def this(regulator: WordRegulator) {
    this(new WordsAggregator, regulator)
  }
  val wordCountCache = new WordCountCache
  val addFlow = regulator.filterWords _ andThen aggregator.aggregate andThen wordCountCache.addToCache
  val getFlow = regulator.filterWord _ andThen wordCountCache.getCount
}
