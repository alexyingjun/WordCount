package com.synechron.wordcounter.core.flow.aggregator

/**
 * Class to aggregate the input words
 */
case class WordsAggregator(){
  /**
   * Group by the input words to a map
   * @return
   *    A map where key is the unique word and value is its count
   */
  def aggregate: Seq[String] => Map[String, Long] = input => input.groupBy(identity).map(x=>(x._1,x._2.size))
}
