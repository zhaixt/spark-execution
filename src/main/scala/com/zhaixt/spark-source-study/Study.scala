package com.zhaixt.spark_source_study

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by zhaixt on 2017/6/30.
 */
object Study {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CollaborationFilterTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data")
    val sc = new SparkContext(conf)
    //第一列位用户编号，第二列位产品编号，第三列的评分Rating为Double类型
    val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\als\\ml-10m\\ml-10M100K\\ratings.dat")
    println("partition num:" + data.getNumPartitions)
    val dates = ArrayBuffer[String]()

    val a = sc.parallelize(1 to 20,2)
    def mapTerFunc(a:Int):Int = {
      a*3
    }
    val mapResult = a.map(mapTerFunc)
    println(mapResult.collect().mkString(","))

  }
  def pt_convert(idx:Int,ds:Iterator[String],seq:Int):Iterator[String]={
    if(seq==idx) ds else Iterator()
  }
}
