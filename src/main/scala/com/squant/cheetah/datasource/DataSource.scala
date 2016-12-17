package com.squant.cheetah.datasource

trait DataSource {
  def update(): Unit

  def clear():Unit
}