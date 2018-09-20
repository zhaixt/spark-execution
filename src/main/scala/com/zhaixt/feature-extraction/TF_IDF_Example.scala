package com.zhaixt.feature_extraction


import java.util

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
// $example on$
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

/**
 * 特征提取
 * Created by zhaixiaotong on 2017-6-15.
 * 来自spark源码，类名：TFIDFExample
 */
object TF_IDF_Example {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TFIDFExample").setMaster("local[2]")
    val sc = new SparkContext(conf)

    // $example on$
    // Load documents (one per line).
    val documents: RDD[Seq[String]] = sc.textFile("D:\\ProgrammingStudy\\spark-data\\data\\mllib\\kmeans_data.txt")
      .map(_.split(" ").toSeq)

    val hashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)

    // While applying HashingTF only needs a single pass to the data, applying IDF needs two passes:
    // First to compute the IDF vector and second to scale the term frequencies by IDF.
    tf.cache()
    val idf = new IDF().fit(tf)
    val tfidf: RDD[Vector] = idf.transform(tf)

    // spark.mllib IDF implementation provides an option for ignoring terms which occur in less than
    // a minimum number of documents. In such cases, the IDF for these terms is set to 0.
    // This feature can be used by passing the minDocFreq value to the IDF constructor.
    val idfIgnore = new IDF(minDocFreq = 2).fit(tf)
    val tfidfIgnore: RDD[Vector] = idfIgnore.transform(tf)
    // $example off$

    println("tfidf: ")
    tfidf.foreach(x => println(x))

    println("tfidfIgnore: ")
    tfidfIgnore.foreach(x => println(x))

    sc.stop()
  }
}
