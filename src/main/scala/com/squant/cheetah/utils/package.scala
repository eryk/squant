package com.squant.cheetah

package object utils {

  import com.typesafe.config._

  lazy val config = ConfigFactory.load()
}
