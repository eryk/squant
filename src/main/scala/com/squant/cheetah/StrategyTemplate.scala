package com.squant.cheetah

import com.typesafe.scalalogging.LazyLogging

trait Strategy extends LazyLogging{
  def strategy()
}
