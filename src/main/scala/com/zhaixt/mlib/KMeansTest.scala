package com.zhaixt.mlib

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by zhaixiaotong on 2017-5-17.
 */
object KMeansTest {
  def main(args: Array[String]): Unit = {
    //1读取样本数据
    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")
    val sc = new SparkContext(conf)
    val data_path = "D:\\ProgrammingStudy\\spark-data\\kmeans_test_source.txt"
    val data = sc.textFile(data_path)
    val examples = data.map { line =>
      Vectors.dense(line.split(" ").map(_.toDouble))
    }.cache()
    val numExamples = examples.count()
    println(s"numExampoles = $numExamples")//在""之前加s代表可以包含变量


    //2 建立模型
   // k:聚类个数，默认2；maxIterations：迭代次数，默认20；runs：并行度，默认1；
    //initializationMode：初始中心算法，默认"k-means||"；initializationSteps：初始步长，默认5；epsilon：中心距离阈值，默认1e-4；seed：随机种子。
    val k = 2;
    val maxIterations = 20
    val runs = 2
    val initializationMode = "k-means||"
    val model = KMeans.train(examples,k,maxIterations,runs,initializationMode)

    //3 计算测试误差
    val cost = model.computeCost(examples)
    println(s"Total cost = $cost.")
  }




}
