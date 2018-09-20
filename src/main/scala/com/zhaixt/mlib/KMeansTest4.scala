package com.zhaixt.mlib

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by zhaixiaotong on 2017-5-17.
 *
 */
object KMeansTest4 {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)

    val data_path = "D:\\ProgrammingStudy\\spark-data\\kmeans4_training_data.csv"

    val rawTrainingData = sc.textFile(data_path)
   val pasedVal = rawTrainingData.map(line=>{println(line)})



    val parsedTrainingData =
      rawTrainingData.filter(!isColumnNameLine(_)).map(line => {
        Vectors.dense(line.split(",").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
      }).cache()

    // Cluster the data into two classes using KMeans

    val numClusters = 8
    val numIterations = 30 //表示方法单次运行最大的迭代次数。
    val runTimes = 3 //run 表示算法被运行的次数。K-means 算法不保证能返回全局最优的聚类结果，所以在目标数据集上多次跑 K-means 算法，有助于返回最佳聚类结果。
    var clusterIndex: Int = 0
    val clusters: KMeansModel =
      KMeans.train(parsedTrainingData, numClusters, numIterations, runTimes)

    println("Cluster Number:" + clusters.clusterCenters.length)

    println("Cluster Centers Information Overview:")
    clusters.clusterCenters.foreach(
      x => {
        println("Center Point of Cluster " + clusterIndex + ":")
        println(x)
        clusterIndex += 1
      })

    //begin to check which cluster each test data belongs to based on the clustering result

    val rawTestData = sc.textFile("D:\\ProgrammingStudy\\spark-data\\kmeans4_test_data.csv")
    val parsedTestData = rawTestData.map(line => {
      Vectors.dense(line.split(",").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
    })
    parsedTestData.collect().foreach(testDataLine => {
      val predictedClusterIndex:
      Int = clusters.predict(testDataLine)
      println("The data " + testDataLine.toString + " belongs to cluster " +
        predictedClusterIndex)
    })

    println("Spark MLlib K-means clustering test finished.")
  }

  private def isColumnNameLine(line: String): Boolean = {
    if (line != null && line.contains("Channel")) true
    else false
  }
  private def isColumn(line:String):Boolean = {
    if(line!= null && line.contains("hehe")) true
      else false
  }
}
