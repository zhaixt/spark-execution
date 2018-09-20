package com.zhaixt.feature_processing

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by zhaixiaotong on 2017-6-6.
 * https://my.oschina.net/sunmin/blog/723854
 * 数据降维
 * 主成分分析PCA
 * 设法将原来具有一定相关行（比如 P个指标）的指标
 * 重新组合成一组新的互相无关的综合指标来代替原来的指标，从而实现数据降维的目的
 */
object PCATest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CollaborationFilterTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data")
    val sc = new SparkContext(conf)
    //第一列位用户编号，第二列位产品编号，第三列的评分Rating为Double类型
//    val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\PCA\\PCA_test1.txt")
    val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\PCA\\pca2.data")
    val pcaTrain = data.map(_.split(" ").map(_.toDouble)).map(line=> Vectors.dense(line))
    val rm = new RowMatrix(pcaTrain)
    println("rm rows :"+rm.numRows(),rm.numCols())
    val pc = rm.computePrincipalComponents(3)//提取主成分，设置主成分个数为３
    val rows = pc.numRows
    val cols = pc.numCols
    println("pc rows and cols:"+rows,cols)
    val mx = rm.multiply(pc)//创建主成分矩阵
    mx.rows.foreach(println)
    sc.stop()
  }
}
