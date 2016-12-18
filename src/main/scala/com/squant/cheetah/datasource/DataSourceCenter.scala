package com.squant.cheetah.datasource

//todo different source download
object DataSourceCenter extends App{
  TushareDataSource.update();

  SinaDataSource.update();

  THSDataSource.update();
}
