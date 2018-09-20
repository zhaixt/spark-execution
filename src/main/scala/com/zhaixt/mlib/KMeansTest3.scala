package com.zhaixt.mlib

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
/**
 * Created by zhaixiaotong on 2017-5-17.
 */
object KMeansTest3 {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)

    val data_path = "D:\\ProgrammingStudy\\spark-data\\kmeans_test_source.txt"

    val data = sc.textFile(data_path)

    val parsedData =data.map(s => Vectors.dense(s.split(' ').map(_.trim.toDouble))).cache()

    //设置簇的个数为3

    val numClusters =2

    //迭代20次

    val numIterations= 20

    //运行10次,选出最优解

    val runs=10

    //设置初始K选取方式为k-means++

    val initMode = "k-means||"

    val clusters = new KMeans().

      setInitializationMode(initMode).

      setK(numClusters).

      setMaxIterations(numIterations).

      run(parsedData)

    //打印出测试数据属于哪个簇

    println(parsedData.map(v=> v.toString() + " belong to cluster :" +clusters.predict(v)).collect().mkString("\n"))

    // Evaluateclustering by computing Within Set Sum of Squared Errors

    val WSSSE = clusters.computeCost(parsedData)

    println("WithinSet Sum of Squared Errors = " + WSSSE)

    val a21 =clusters.predict(Vectors.dense(1.2,1.3))

    val a22 =clusters.predict(Vectors.dense(4.1,4.2))

    //打印出中心点

    println("Clustercenters:")

    for (center <-clusters.clusterCenters) {

      println(" "+ center)

    }

    println("Prediction of (1.2,1.3)-->"+a21)

    println("Prediction of (4.1,4.2)-->"+a22)

  }
}
