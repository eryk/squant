import java.time.LocalDateTime

import com.squant.cheetah.Feeds
import com.squant.cheetah.domain.DAY

/**
  * Created by eryk on 17-4-13.
  */
object StockSelector extends App {

  var symbols = Feeds.symbols()

  symbols = symbols.filter(symbol => !symbol.name.contains("ST") && symbol.pe > 0 && symbol.pe < 200 && Feeds.realtime(symbol.code).close < 30)
  symbols.par.foreach(symbol => find(symbol.code))

  def find(symbol: String) = {
    try {
      val bars = Feeds.ktype(symbol, DAY, index = false).filter(bar => bar.close > 0)
      if (bars.length > 13) {
        for (i <- 13 until bars.size) {

          var loop = i
          var signal = true
          while (loop > 0 && signal) {
            if (bars(loop).close > bars(loop - 4).close)
              signal = false
            loop -= 1
            if (loop == i - 9 && signal && bars(i).date.toLocalDate.isEqual(LocalDateTime.now().plusDays(-2).toLocalDate)) {
              println(bars(i).code + "\t" + bars(i).date)
            }
          }
        }
      }
    } catch {
      case ex: Exception =>
    }


  }

}
