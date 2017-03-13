package com.squant.cheetah.datasource

trait Rule[I, O] {
  def verify(value: I): Boolean

  def process(value: I): O
}

class NoneValue[V](defaultValue: V) extends Rule[String, V] {
  override def verify(value: String): Boolean = {
    value match {
      case value if (value == null || value == "") => false
      case _ => true
    }
  }

  override def process(value: String): V = defaultValue
}

class FixedValue[V](input: Set[String], defaultValue: V) extends Rule[String, V] {
  override def verify(value: String): Boolean = !input.contains(value)

  override def process(value: String): V = defaultValue
}

class OutofRangeValue[V <% Comparable[V]](start: V, stop: V, defaultValue: V) extends Rule[V, V] {
  override def verify(value: V): Boolean = {
    if (value.compareTo(start) > 0 && value.compareTo(stop) < 0) {
      true
    } else
      false
  }

  override def process(value: V): V = defaultValue
}

object Rules extends App {
  val rule = new OutofRangeValue[Float](-10,10,0);
  if(rule.verify(-200)){
    printf("-200")
  }else{
    printf(s"${rule.process(-200)}")
  }
}

class DataCleaner