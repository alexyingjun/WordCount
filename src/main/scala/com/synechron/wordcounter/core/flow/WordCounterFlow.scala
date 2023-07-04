package com.synechron.wordcounter.core.flow

import com.synechron.wordcounter.core.cache.WordCountCache
import com.synechron.wordcounter.core.flow
import com.synechron.wordcounter.core.flow.aggregator.WordsAggregator
import com.synechron.wordcounter.core.flow.regulator.WordRegulator
import com.synechron.wordcounter.core.flow.regulator.validator.WordValidator

/**
 * Class to define the flow for add and get features
 *
 * @param aggregator
 *    WordsAggregator in which defines how to aggregate the input words
 * @param regulator
 *    WordsRegulator in which defines how to regulate the input words
 */
case class WordCounterFlow(aggregator: WordsAggregator, regulator: WordRegulator) {

  /**
   * Default constructor uses default regulator and aggregator
   */
  def this() {
    this(new WordsAggregator, flow.regulator.WordRegulator(new WordValidator))
  }

  /**
   * Constructor uses default regulator but designated aggregator
   */
  def this(aggregator: WordsAggregator) {
    this(aggregator, flow.regulator.WordRegulator(new WordValidator))
  }

  /**
   * Constructor uses default aggregator but designated regulator
   */
  def this(regulator: WordRegulator) {
    this(new WordsAggregator, regulator)
  }
  val wordCountCache = new WordCountCache

  /**
   * Flow to add words:
   * 1. filter invalid words and change to lowercase
   * 2. aggregate the words to map(word, count)
   * 3. add to cache/storage
   */
  val addFlow = regulator.filterWords _ andThen aggregator.aggregate andThen wordCountCache.addToCache

  /**
   * Flow to get count:
   * 1. filter invalid words and change to lowercase
   * 2. get count of the word from cache/storage
   */
  val getFlow = regulator.filterWord _ andThen wordCountCache.getCount
}
