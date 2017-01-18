package com.squant.cheetah.datasource

import java.time.LocalDateTime

trait DataSource {

  //初始化数据源
  def init()

  //每个周期更新数据
  def update(start:LocalDateTime,stop:LocalDateTime)

  //清空数据源
  def clear()
}