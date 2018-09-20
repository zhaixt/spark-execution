package com.zhaixt.afe

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by zhaixt on 2018/5/7.
  */
object AfeUtilsTest {
  def main(args:Array[String]):Unit = {

  }
  def testBlackList():Unit  = {
    var sc:SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("AfeUtilsTest"))
    var sqlContext = new SQLContext(sc)
    var lines = scala.io.Source.fromFile("D:\\ProgrammingStudy\\spark-data\\afe\\blacklist.txt")

  }
}
