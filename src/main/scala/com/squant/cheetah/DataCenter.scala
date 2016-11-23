package com.squant.cheetah

import scala.sys.process._

class DataCenter {
  def init() = {
    "python3.5 " + getProjectDir() + "/script/Download.py" !
  }
}
