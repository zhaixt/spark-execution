package com.zhaixt.classification_regression

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.{StandardScaler, StandardScalerModel}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{GradientBoostedTrees, DecisionTree}
import org.apache.spark.mllib.tree.configuration.{BoostingStrategy, Algo}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by zhaixiaotong on 2017-5-23.
  * http://www.cnblogs.com/haozhengfei/p/8b9cb1875288d9f6cfc2f5a9b2f10eac.html
 * GBDT，是梯度上升决策树，制决定最终的结果。
   GBDT通常只有第一个树是完整的，当预测值和真实值有一定差距时（残差），下一棵树的构建会拿到上一棵树最终的残差作为当前树的输入。
   GBDT每次关注的不是预测错误的样本，没有对错一说，只有离标准相差的远近
 * 运行参数： E:\IDEA_Projects\mlib\data\GBDT\train E:\IDEA_Projects\mlib\data\GBDT\train\model 10 local
  */
object GBDTTest {
   def main(args: Array[String]) {
     Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
     if (args.length < 4) {
       System.err.println("Usage: DecisionTrees <inputPath> <modelPath> <maxDepth> <master> [<AppName>]")
       System.err.println("eg: hdfs://192.168.57.104:8020/user/000000_0 10 0.1 spark://192.168.57.104:7077 DecisionTrees")
       System.exit(1)
     }
     val appName = if (args.length > 4) args(4) else "DecisionTrees"
     val conf = new SparkConf().setAppName(appName).setMaster(args(3))
     val sc = new SparkContext(conf)

     val traindata: RDD[LabeledPoint] = MLUtils.loadLabeledPoints(sc, args(0))
     val features = traindata.map(_.features)
     val scaler: StandardScalerModel = new StandardScaler(withMean = true, withStd = true).fit(features)
     val train: RDD[LabeledPoint] = traindata.map(sample => {
       val label = sample.label
       val feature = scaler.transform(sample.features)
       new LabeledPoint(label, feature)
     })
     val splitRdd: Array[RDD[LabeledPoint]] = traindata.randomSplit(Array(1.0, 9.0))
     val testData: RDD[LabeledPoint] = splitRdd(0)
     val realTrainData: RDD[LabeledPoint] = splitRdd(1)

     val boostingStrategy: BoostingStrategy = BoostingStrategy.defaultParams("Classification")
     boostingStrategy.setNumIterations(3)
     boostingStrategy.treeStrategy.setNumClasses(2)
     boostingStrategy.treeStrategy.setMaxDepth(args(2).toInt)
     boostingStrategy.setLearningRate(0.8)
     //  boostingStrategy.treeStrategy.setCategoricalFeaturesInfo(Map[Int, Int]())
     val model = GradientBoostedTrees.train(realTrainData, boostingStrategy)

     val labelAndPreds = testData.map(point => {
       val prediction = model.predict(point.features)
       (point.label, prediction)
     })
     val acc = labelAndPreds.filter(r => r._1 == r._2).count.toDouble / testData.count()

     println("Test Error = " + acc)

     model.save(sc, args(1))


   }
 }
