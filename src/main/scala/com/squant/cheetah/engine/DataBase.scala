package com.squant.cheetah.engine

import java.time.LocalDateTime

import com.squant.cheetah.DataEngine
import com.squant.cheetah.domain.{BarType, DAY}
import com.squant.cheetah.utils._
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

case class Row(index: String, timestamp: Long, record: Map[String, String])

trait DataBase {

  def isTableExist(name: String): Boolean

  def createTable(name: String)

  def deleteTable(name: String)

  def toDB(tableName: String, rowSet: Set[Row])

  def fromDB(tableName: String, start: LocalDateTime, stop: LocalDateTime): List[Row]

  def close()
}

class HBase(zookeeper: String = "127.0.0.1", root: String = "/hbase") extends DataBase {

  private val conn = {
    val configuration = HBaseConfiguration.create()
    configuration.set("hbase.zookeeper.quorum", zookeeper)
    configuration.set("zookeeper.znode.parent", root)
    ConnectionFactory.createConnection(configuration)
  }

  private val admin: Admin = conn.getAdmin

  override def isTableExist(name: String): Boolean = {
    admin.tableExists(TableName.valueOf(name))
  }

  override def createTable(name: String) = {
    val tableName = TableName.valueOf(name)
    if (admin.tableExists(tableName)) {
      System.out.println("table is exists!")
    } else {
      val hTableDescriptor = new HTableDescriptor(tableName)
      val hColumnDescriptor = new HColumnDescriptor("f")
      hColumnDescriptor.setCompressionType(Algorithm.GZ)
      hTableDescriptor.addFamily(hColumnDescriptor)
      admin.createTable(hTableDescriptor)
    }
  }

  override def deleteTable(name: String) = {
    val tableName = TableName.valueOf(name)
    if (admin.tableExists(tableName)) {
      admin.disableTable(tableName)
      admin.deleteTable(tableName)
    }
  }

  override def toDB(tableName: String, rowSet: Set[Row]) = {
    val puts = scala.collection.mutable.Buffer[Put]()

    for (row <- rowSet) {
      val put = new Put((row.index).getBytes("utf8"))
      for ((k, v) <- row.record)
        put.addColumn("f".getBytes, k.getBytes("utf8"), row.timestamp, v.getBytes("utf8"))
      puts.append(put)
    }

    val table = conn.getTable(TableName.valueOf(tableName))
    table.put(puts.asJava)
    table.close()
  }

  override def fromDB(tableName: String, start: LocalDateTime, stop: LocalDateTime): List[Row] = {
    val table = conn.getTable(TableName.valueOf(tableName))
    val scan = new Scan()
    scan.setTimeRange(localDateTimeToLong(start), localDateTimeToLong(stop))
    val resultScanner = table.getScanner(scan)
    val iter = resultScanner.iterator()
    val resultList: ListBuffer[Row] = scala.collection.mutable.ListBuffer[Row]()

    while (iter.hasNext) {
      val t = iter.next()
      val columns = t.listCells().asScala
      val values = columns.map((cell: Cell) => (Bytes.toString(cell.getQualifierArray), Bytes.toString(cell.getValueArray)))

      resultList.append(Row(Bytes.toString(t.getRow), columns.head.getTimestamp, values.toMap))
    }

    resultScanner.close()
    resultList.toList
  }

  override def close(): Unit = {
    admin.close()
    conn.close()
  }
}

object HBase extends App {

  val values = DataEngine.ktype("000001",DAY,start = LocalDateTime.now.plusDays(-30))
  values.foreach(print)

//  val hbase = new HBase()
//  hbase.createTable("test")
//  hbase.toDB()
}
