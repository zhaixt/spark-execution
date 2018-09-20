package com.zhaixt.sparksql

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author zhaixt
  */
object BasicMergeTest {

  def main(args: Array[String]): Unit = {
    mergeTest()
  }

  def mergeTest(): Unit = {
    val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("BasicMergeTest"))
    val sqlContext = new SQLContext(sc)

    //(1) dw data mock
    val multiRDD = sc.textFile("D:\\ProgrammingStudy\\spark-data\\afe\\basic_merge_sample.txt").map(_.split("&"))

    val schema = StructType(
      List(
        StructField("loanappid", StringType, true),
        StructField("basic", StringType, true),
        StructField("update_time", StringType, true)
      )
    )

    val rowRDD = multiRDD.map(p => Row(p(0), p(1), p(2)))
    val multiDataFrame = sqlContext.createDataFrame(rowRDD, schema)
    multiDataFrame.registerTempTable("afe_raw_loan_multi_detail")

    val df = sqlContext.sql("select * from afe_raw_loan_multi_detail")

    var currentLoan = ""
    var nextLoan = ""

//    var mergeBuffer: MergeBuffer = null

    val allBuffer: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map()

    var broadcastBuffer: Broadcast[scala.collection.mutable.Map[String, String]] = sc.broadcast(scala.collection.mutable.Map())

     println("size: " + df.rdd.count())

    val size = df.rdd.count()
    var counter = 0

    //collect()表示拉到本地driver执行
    //df.rdd.collect().foreach(row => {
    df.rdd.foreach(row => {

      counter = counter + 1

      val loanappid = row.getAs[String]("loanappid")
      val basic = row.getAs[String]("basic")
      val update_time = row.getAs[String]("update_time")
      println("loanappid:"+loanappid+",basic:"+basic+",update_time:"+update_time)

    })




    println("=======================================")
    println(allBuffer.size)
    println("broadcastBuffer:" + broadcastBuffer.value.size)
    //(2) merge basic properties
    //val mergeRDD = allBuffer.map(p => Row(p._1, p._2))
    val trainRdd = sc
      // Convert Map to Seq so it can passed to parallelize
      .parallelize(broadcastBuffer.value.toSeq)
      .map { case (loanid, basic) => {
        (loanid, basic)
      }
      }

    val schema2 = StructType(
      List(
        StructField("loanappid", StringType, true),
        StructField("basic", StringType, true)
      )
    )

    val mergeRDD = trainRdd.map(e => Row(e._1, e._2))

    val mergeDataFrame = sqlContext.createDataFrame(mergeRDD, schema2)
    mergeDataFrame.registerTempTable("merge_table")

    val mergeDF = sqlContext.sql("select * from merge_table")

    mergeDF.rdd.foreach(row => {
      val loanappid = row.getAs[String]("loanappid")
      val basic = row.getAs[String]("basic")

      println(loanappid)
      println(basic)

    })

  }

}
