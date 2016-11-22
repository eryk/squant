package com.squant

case class Symbol(code: String, //代码
                  name: String, //名称
                  industry: String, //所属行业
                  area: String, //地区
                  pe: Float, //市盈率
                  outstanding: Float, //流通股本
                  totals: Float, //总股本(万)
                  totalAssets: Float, //总资产(万)
                  liquidAssets: Float, //流动资产
                  fixedAssets: Float, //固定资产
                  reserved: Float, //公积金
                  reservedPerShare: Float, //每股公积金
                  esp: Float, //每股收益
                  bvps: Float, //每股净资
                  pb: Float, //市净率
                  timeToMarket: String, //上市日期
                  undp: Float, //未分利润
                  perundp: Float, // 每股未分配
                  rev: Float, //收入同比(%)
                  profit: Float, //利润同比(%)
                  gpr: Float, //毛利率(%)
                  npr: Float, //净利润率(%)
                  holders: Long //股东人数
                 )

package object cheetah {

  def mapToSymbol(map: Map[String, String]): Symbol =
    new Symbol(
      map.get("code").get, //代码
      map.get("name").get, //名称
      map.get("industry").get, //所属行业
      map.get("area").get, //地区
      map.get("pe").get.toFloat, //市盈率
      map.get("outstanding").get.toFloat, //流通股本
      map.get("totals").get.toFloat, //总股本(万)
      map.get("totalAssets").get.toFloat, //总资产(万)
      map.get("liquidAssets").get.toFloat, //流动资产
      map.get("fixedAssets").get.toFloat, //固定资产
      map.get("reserved").get.toFloat, //公积金
      map.get("reservedPerShare").get.toFloat, //每股公积金
      map.get("esp").get.toFloat, //每股收益
      map.get("bvps").get.toFloat, //每股净资
      map.get("pb").get.toFloat, //市净率
      map.get("timeToMarket").get, //上市日期
      map.get("undp").get.toFloat, //未分利润
      map.get("perundp").get.toFloat, //每股未分配
      map.get("rev").get.toFloat, //收入同比(%)
      map.get("profit").get.toFloat, //利润同比(%)
      map.get("gpr").get.toFloat, //毛利率(%)
      map.get("npr").get.toFloat, //净利润率(%)
      map.get("holders").get.toLong //股东人数
    )
}
