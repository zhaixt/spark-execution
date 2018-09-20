package com.zhaixt.classification_regression

import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}
import com.zhaixt.Constant
import org.apache.spark.mllib.clustering.LDA
import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by zhaixt on 2017/7/13.
  * 隐含狄利克雷分配(Latent Dirichlet Allocation)
  */
object LDAExample {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("LDATest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)

    //     val data = "D:\\ProgrammingStudy\\spark-data\\decision_tree\\train_tree.txt"

    // Load and parse the data file.
    // Cache the data since we will use it again to compute training error.
    val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\sample_lda_data.txt")
    val parsedData = data.map(s => Vectors.dense(s.trim.split(' ').map(_.toDouble)))
    val corpus = parsedData.zipWithIndex.map((_.swap)).cache()

    val ldaModel = new LDA().setK(3).run(corpus)
    //打印主题
    println("Learned topics (as distributions over vocab of " + ldaModel.vocabSize + " words):")
    val topics = ldaModel.topicsMatrix
    for (topic <- Range(0, 3)) {
      print("Topic" + topic + ":")
      for (word <- Range(0, ldaModel.vocabSize)) {
        print(" " + topics(word, topic))
      }
      println()
    }
  }
}
