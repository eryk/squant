package com.squant.cheetah.strategy

import com.squant.cheetah.DataEngine
import com.squant.cheetah.datasource.DailyKTypeDataSource
import com.squant.cheetah.domain.DAY
import com.tictactec.ta.lib.{Core, MAType, MInteger, RetCode}

object Indicators extends App {

  private val core: Core = new Core()


  val bars = DataEngine.ktype("000001", DAY, index = true)
  bars.foreach(println)
  val array = bars.map(bar => bar.close.toDouble).toArray
  val result = boll(array)
  println(result(0).last + "\t" + result(1).last + "\t" + result(2).last)

  def sma(prices: Array[Double], ma: Int): Array[Double] = {
    val tempOutPut: Array[Double] = new Array[Double](prices.length)
    val output: Array[Double] = new Array[Double](prices.length)
    val begin: MInteger = new MInteger()
    val length: MInteger = new MInteger()
    var retCode: RetCode = RetCode.InternalError
    begin.value = -1
    length.value = -1
    retCode = core.sma(0, prices.length - 1, prices, ma, begin, length, tempOutPut)
    var i = ma - 1
    while (i < prices.length) {
      output(i) = tempOutPut(i - ma + 1)
      i += 1
    }
    return output
  }

  def ema(prices: Array[Double], ma: Int): Array[Double] = {
    val tempOutPut: Array[Double] = new Array[Double](prices.length)
    val output: Array[Double] = new Array[Double](prices.length)
    val begin: MInteger = new MInteger()
    val length: MInteger = new MInteger()
    var retCode: RetCode = RetCode.InternalError
    begin.value = -1
    length.value = -1
    retCode = core.ema(0, prices.length - 1, prices, ma, begin, length, tempOutPut)
    var i = ma - 1
    while (i < prices.length) {
      output(i) = tempOutPut(i - ma + 1)
      i += 1
    }
    return output
  }

  def macd(prices: Array[Double], optInFastPeriod: Int = 12, optInSlowPeriod: Int = 26, optInSignalPeriod: Int = 9): Array[Array[Double]] = {
    val tempoutput1: Array[Double] = new Array[Double](prices.length)
    val tempoutput2: Array[Double] = new Array[Double](prices.length)
    val tempoutput3: Array[Double] = new Array[Double](prices.length)
    val output: Array[Array[Double]] = Array(new Array[Double](prices.length), new Array[Double](prices.length), new Array[Double](prices.length))
    val result1: Array[Double] = new Array[Double](prices.length)
    val result2: Array[Double] = new Array[Double](prices.length)
    val result3: Array[Double] = new Array[Double](prices.length)
    val begin: MInteger = new MInteger()
    val length: MInteger = new MInteger()
    var retCode: RetCode = RetCode.InternalError
    begin.value = -1
    length.value = -1
    val optInFastMAType: MAType = MAType.Ema
    val optInSlowMAType: MAType = MAType.Ema
    val optInSignalMAType: MAType = MAType.Ema
    retCode = core.macdExt(0, prices.length - 1, prices, optInFastPeriod, optInFastMAType, optInSlowPeriod, optInSlowMAType, optInSignalPeriod, optInSignalMAType, begin, length, tempoutput1, tempoutput2, tempoutput3)

    var i = begin.value
    while (i < prices.length) {
      result1(i) = tempoutput1(i - begin.value)
      result2(i) = tempoutput2(i - begin.value)
      result3(i) = tempoutput3(i - begin.value)
      i += 1
    }
    i = 0
    while (i < prices.length) {
      output(0)(i) = result1(i)
      output(1)(i) = result2(i)
      output(2)(i) = (output(0)(i) - output(1)(i)) * 2
      i += 1
    }
    return output
  }

  def boll(prices: Array[Double], optInTimePeriod: Int = 20, optInNbDevUp: Double = 2, optInNbDevDn: Double = 2): Array[Array[Double]] = {
    val optInMAType: MAType = MAType.Sma
    val tempoutput1: Array[Double] = new Array[Double](prices.length)
    val tempoutput2: Array[Double] = new Array[Double](prices.length)
    val tempoutput3: Array[Double] = new Array[Double](prices.length)
    val output: Array[Array[Double]] = Array(new Array[Double](prices.length), new Array[Double](prices.length), new Array[Double](prices.length))
    val result1: Array[Double] = new Array[Double](prices.length)
    val result2: Array[Double] = new Array[Double](prices.length)
    val result3: Array[Double] = new Array[Double](prices.length)
    val begin: MInteger = new MInteger()
    val length: MInteger = new MInteger()
    var retCode: RetCode = RetCode.InternalError
    begin.value = -1
    length.value = -1
    retCode = core.bbands(0, prices.length - 1, prices, optInTimePeriod, optInNbDevUp, optInNbDevDn, optInMAType, begin, length, tempoutput1, tempoutput2, tempoutput3)
    var i = optInTimePeriod - 1
    while (i < prices.length) {
      result1(i) = tempoutput1(i - optInTimePeriod + 1)
      result2(i) = tempoutput2(i - optInTimePeriod + 1)
      result3(i) = tempoutput3(i - optInTimePeriod + 1)
      i += 1
    }
    i = 0
    while (i < prices.length) {
      output(0)(i) = result1(i)
      output(1)(i) = result2(i)
      output(2)(i) = result3(i)
      i += 1
    }
    output
  }

  //
  //  def dma(prices: Array[Double]): Array[Array[Double]] = {
  //    val ma10: Array[Double] = sma(prices, 10)
  //    val ma50: Array[Double] = sma(prices, 50)
  //    val dif: Array[Double] = new Array[Double](ma10.length)
  //    var i: Int = 0
  //    while (i < dif.length) {
  //      {
  //        dif(i) = ma10(i) - ma50(i)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    val ama: Array[Double] = sma(dif, 10)
  //    val result: Array[Array[Double]] = Array(dif, ama)
  //    return result
  //  }

  //  def kdj(high: Array[Double], low: Array[Double], close: Array[Double]): Array[Array[Double]] = {
  //    val length: Int = high.length
  //    val outSlowK: Double = new Array[Double](high.length)
  //    val outSlowD: Double = new Array[Double](high.length)
  //    val outSlowJ: Double = new Array[Double](high.length)
  //    val RSV: Array[Double] = new Array[Double](high.length)
  //    var i: Int = 0
  //    while (i < length) {
  //      {
  //        if (i >= 8) {
  //          var start: Int = i - 8
  //          var high9: Double = Double.MIN_VALUE
  //          var low9: Double = Double.MAX_VALUE
  //          while (start <= i) {
  //            {
  //              if (high(start) > high9) {
  //                high9 = high(start)
  //              }
  //              if (low(start) < low9) {
  //                low9 = low(start)
  //              }
  //              start += 1
  //            }
  //          }
  //          RSV(i) = (close(i) - low9) / (high9 - low9) * 100
  //        }
  //        else {
  //          RSV(i) = 0d
  //        }
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = 0
  //    while (i < length) {
  //      {
  //        if (i > 1) {
  //          outSlowK(i) = 2 / 3d * outSlowK(i - 1) + 1 / 3d * RSV(i)
  //          outSlowD(i) = 2 / 3d * outSlowD(i - 1) + 1 / 3d * outSlowK(i)
  //          outSlowJ(i) = 3 * outSlowK(i) - 2 * outSlowD(i)
  //          if (outSlowJ(i) > 100) {
  //            outSlowJ(i) = 100
  //          }
  //          else if (outSlowJ(i) < 0) {
  //            outSlowJ(i) = 0
  //          }
  //        }
  //        else {
  //          outSlowK(i) = 50
  //          outSlowD(i) = 50
  //          outSlowJ(i) = 50
  //        }
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    val result: Array[Array[Double]] = Array(outSlowK, outSlowD, outSlowJ)
  //    return result
  //  }
  //
  //  def kdj(stockDataList: Nothing): Array[Array[Double]] = {
  //    val closes: Array[Double] = new Array[Double](stockDataList.size)
  //    val high: Array[Double] = new Array[Double](stockDataList.size)
  //    val low: Array[Double] = new Array[Double](stockDataList.size)
  //    var i: Int = 0
  //    while (i < stockDataList.size) {
  //      {
  //        closes(i) = stockDataList.get(i).get(StockConstants.CLOSE)
  //        high(i) = stockDataList.get(i).get(StockConstants.HIGH)
  //        low(i) = stockDataList.get(i).get(StockConstants.LOW)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return kdj(high, low, closes)
  //  }
  //
  //  def rsi(prices: Array[Double], period: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.rsi(0, prices.length - 1, prices, period, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < period) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = period
  //    while (0 < i && i < (prices.length)) {
  //      {
  //        output(i) = tempOutPut(i - period)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def sar(highPrices: Array[Double], lowPrices: Array[Double], optInAcceleration: Double, optInMaximum: Double): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val tempoutput: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.sar(0, lowPrices.length - 1, highPrices, lowPrices, optInAcceleration, optInMaximum, begin, length, tempoutput)
  //    var i: Int = 1
  //    while (i < lowPrices.length) {
  //      {
  //        output(i) = tempoutput(i - 1)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def adx(lowPrices: Array[Double], highPrices: Array[Double], closePrices: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.adx(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < lowPrices.length - length.value) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = lowPrices.length - length.value
  //    while (0 < i && i < (lowPrices.length)) {
  //      {
  //        output(i) = tempOutPut(i - (lowPrices.length - length.value))
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def adxr(lowPrices: Array[Double], highPrices: Array[Double], closePrices: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.adxr(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < lowPrices.length - length.value) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = lowPrices.length - length.value
  //    while (0 < i && i < (lowPrices.length)) {
  //      {
  //        output(i) = tempOutPut(i - (lowPrices.length - length.value))
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def cci(highPrices: Array[Double], lowPrices: Array[Double], closePrices: Array[Double], inTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.cci(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < inTimePeriod - 1) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = inTimePeriod - 1
  //    while (0 < i && i < (lowPrices.length)) {
  //      {
  //        output(i) = tempOutPut(i - inTimePeriod + 1)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def mfi(highPrices: Array[Double], lowPrices: Array[Double], closePrices: Array[Double], inVolume: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.mfi(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inVolume, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < optInTimePeriod) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = optInTimePeriod
  //    while (0 < i && i < (lowPrices.length)) {
  //      {
  //        output(i) = tempOutPut(i - optInTimePeriod)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def obv(prices: Array[Double], volume: Array[Double]): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.obv(0, prices.length - 1, prices, volume, begin, length, output)
  //    return output
  //  }
  //
  //  def roc(prices: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val tempOutPut: Array[Double] = new Array[Double](prices.length)
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.roc(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < optInTimePeriod - 1) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = optInTimePeriod - 1
  //    while (0 < i && i < (prices.length)) {
  //      {
  //        output(i) = tempOutPut(i - optInTimePeriod + 1)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def rocP(prices: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val tempOutPut: Array[Double] = new Array[Double](prices.length)
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.rocP(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < optInTimePeriod - 1) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = optInTimePeriod - 1
  //    while (0 < i && i < (prices.length)) {
  //      {
  //        output(i) = tempOutPut(i - optInTimePeriod + 1)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def trix(prices: Array[Double], period: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.trix(0, prices.length - 1, prices, period, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < begin.value) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = begin.value
  //    while (0 < i && i < (prices.length)) {
  //      {
  //        output(i) = tempOutPut(i - begin.value)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def willR(highPrices: Array[Double], lowPrices: Array[Double], closePrices: Array[Double], inTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.willR(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < inTimePeriod - 1) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = inTimePeriod - 1
  //    while (0 < i && i < (lowPrices.length)) {
  //      {
  //        output(i) = tempOutPut(i - inTimePeriod + 1)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def ad(highPrices: Array[Double], lowPrices: Array[Double], closePrices: Array[Double], inVolume: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](lowPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.ad(0, lowPrices.length - 1, highPrices, lowPrices, closePrices, inVolume, begin, length, output)
  //    return output
  //  }
  //
  //  def aroon(inHigh: Array[Double], inLow: Array[Double], optInTimePeriod: Int): Array[Array[Double]] = {
  //    val output: Array[Array[Double]] = Array(new Array[Double](inHigh.length), new Array[Double](inHigh.length))
  //    val tempOutPut1: Array[Double] = new Array[Double](inHigh.length)
  //    val tempOutPut2: Array[Double] = new Array[Double](inHigh.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.aroon(0, inHigh.length - 1, inHigh, inLow, optInTimePeriod, begin, length, tempOutPut1, tempOutPut2)
  //    var i: Int = 0
  //    while (i < inHigh.length - length.value) {
  //      {
  //        output(0)(i) = 0
  //        output(1)(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = inHigh.length - length.value
  //    while (0 < i && i < (inHigh.length)) {
  //      {
  //        output(0)(i) = tempOutPut1(i - (inHigh.length - length.value))
  //        output(1)(i) = tempOutPut1(i - (inHigh.length - length.value))
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def aroonOsc(inHigh: Array[Double], inLow: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](inHigh.length)
  //    val tempOutPut: Array[Double] = new Array[Double](inHigh.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.aroonOsc(0, inHigh.length - 1, inHigh, inLow, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < inHigh.length - length.value) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = inHigh.length - length.value
  //    while (0 < i && i < (inHigh.length)) {
  //      {
  //        output(i) = tempOutPut(i - (inHigh.length - length.value))
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def bop(openPrices: Array[Double], highPrices: Array[Double], lowPrices: Array[Double], closePrices: Array[Double]): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](highPrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: Nothing = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.bop(0, lowPrices.length - 1, openPrices, highPrices, lowPrices, closePrices, begin, length, output)
  //    return output
  //  }
  //
  //  def cmo(closePrices: Array[Double], period: Int): Array[Double] = {
  //    val output: Array[Double] = new Array[Double](closePrices.length)
  //    val tempOutPut: Array[Double] = new Array[Double](closePrices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.cmo(0, closePrices.length - 1, closePrices, period, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < closePrices.length - length.value) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = closePrices.length - length.value
  //    while (0 < i && i < (closePrices.length)) {
  //      {
  //        output(i) = tempOutPut(i - (closePrices.length - length.value))
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def kama(prices: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val tempOutPut: Array[Double] = new Array[Double](prices.length)
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.kama(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < optInTimePeriod) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = optInTimePeriod
  //    while (0 < i && i < (prices.length)) {
  //      {
  //        output(i) = tempOutPut(i - optInTimePeriod)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
  //
  //  def trima(prices: Array[Double], optInTimePeriod: Int): Array[Double] = {
  //    val tempOutPut: Array[Double] = new Array[Double](prices.length)
  //    val output: Array[Double] = new Array[Double](prices.length)
  //    val begin: MInteger = new MInteger()
  //    val length: MInteger = new MInteger()
  //    var retCode: RetCode = RetCode.InternalError
  //    begin.value = -1
  //    length.value = -1
  //    retCode = core.trima(0, prices.length - 1, prices, optInTimePeriod, begin, length, tempOutPut)
  //    var i: Int = 0
  //    while (i < optInTimePeriod - 1) {
  //      {
  //        output(i) = 0
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    var i: Int = optInTimePeriod - 1
  //    while (0 < i && i < (prices.length)) {
  //      {
  //        output(i) = tempOutPut(i - optInTimePeriod + 1)
  //      }
  //      ({
  //        i += 1; i - 1
  //      })
  //    }
  //    return output
  //  }
}
