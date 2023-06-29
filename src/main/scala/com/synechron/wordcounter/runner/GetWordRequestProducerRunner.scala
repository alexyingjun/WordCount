package com.synechron.wordcounter.runner

import com.synechron.wordcounter.cache.WordBuffer
import com.synechron.wordcounter.regulator.WordRegulator
import com.synechron.wordcounter.validator.WordValidator

import scala.concurrent.Future

object GetWordRequestProducerRunner extends Getter[Future[Long]]{
//  val regulator = WordRegulator(WordValidator)

//  val getFlow = regulator.filter _ andThen WordBuffer.enqueueGetWordCount
  val getFlow = WordBuffer.enqueueGetWordCount
  override def get(item: Future[Long]): Long = getFlow(item)
}
