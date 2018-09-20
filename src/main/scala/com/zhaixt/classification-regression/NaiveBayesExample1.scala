package com.zhaixt.classification_regression

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext,SparkConf}

/**
 * Created by zhaixiaotong on 2017-6-15.
 * 朴素贝叶斯分类示例
 * http://www.cnblogs.com/jianjunyue/articles/5506139.html
 * 关于某种天气条件下小明是否踢球的
 */
object NaiveBayesExample1 {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("NaiveBayesExample1").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)

    //     val data = "D:\\ProgrammingStudy\\spark-data\\decision_tree\\train_tree.txt"

    // Load and parse the data file.
    // Cache the data since we will use it again to compute training error.
    val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\naive_bayes_data.txt")
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }
    // 把数据的60%作为训练集，40%作为测试集.
    //该函数根据weights权重，将一个RDD切分成多个RDD。第二个参数为random的种子，基本可忽略。
    //返回结果是一个RDD数组
    val splitsRDDArray = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splitsRDDArray(0)
    val test = splitsRDDArray(1)
    println("training count:"+training.count()+",test count:"+test.count())
    //获得训练模型,第一个参数为数据，第二个参数为平滑参数，默认为1，可改
    val model = NaiveBayes.train(training, lambda = 1.0)
    // 准确度为42%,这里是因为测试集数据量比较小的原因，所以偏差较大。

    //对模型进行准确度分析
    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count()/test.count()
    println("accuracy-->" + accuracy)
    println("Prediction of (0.0, 2.0, 0.0, 1.0):" + model.predict(Vectors.dense(0.0, 2.0, 0.0, 1.0)))
  }
}
