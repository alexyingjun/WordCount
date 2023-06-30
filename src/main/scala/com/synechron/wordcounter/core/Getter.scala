package com.synechron.wordcounter.core

trait Getter [I,O]{
  def get(item:I): O
}
