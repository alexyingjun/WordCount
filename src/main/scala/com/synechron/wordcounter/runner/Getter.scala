package com.synechron.wordcounter.runner

trait Getter [T]{
  def get(item:T): Long
}
