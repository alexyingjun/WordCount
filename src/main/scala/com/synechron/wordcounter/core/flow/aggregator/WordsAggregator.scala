package com.synechron.wordcounter.core.flow.aggregator

case class WordsAggregator(){
  def aggregate: Seq[String] => Map[String, Long] = input => input.map(_.toLowerCase).groupBy(identity).map(x=>(x._1,x._2.size))
}
