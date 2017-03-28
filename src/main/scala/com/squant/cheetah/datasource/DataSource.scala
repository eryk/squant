package com.squant.cheetah.datasource

trait DataSource {

  //初始化数据源
  def init(config:TaskConfig)

  //每个周期更新数据
  def update(config:TaskConfig)

  //清空数据源
  def clear()
}