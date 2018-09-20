package com.zhaixt.classification_regression

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by zhaixiaotong on 2017-5-23.
 * http://blog.csdn.net/kwu_ganymede/article/details/52040392
 * 训练数据字段说明：是否见面, 年龄  是否帅  收入(1 高 2 中等 0 少)  是否公务员
 */
object DecisionTreeClassificationTest {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

    val sc = new SparkContext(conf)

    val dataTrainPath = "D:\\ProgrammingStudy\\spark-data\\decision_tree\\train_tree.txt"
    //训练数据
    val dataTrain = sc.textFile(dataTrainPath)

    val dataTestPath = "D:\\ProgrammingStudy\\spark-data\\decision_tree\\test_tree.txt"
    //测试数据
    val dataTest = sc.textFile(dataTestPath)
    //转换成向量
    val treeTrain = dataTrain.map{line=>{
        val parts = line.split(",")
        LabeledPoint(parts(0).toDouble,Vectors.dense(parts(1).split(" ").map(_.toDouble)))
      //        LabelPoint(parts(0).toDouble,Vector.dense(parts(1),split(" ").map(_.toDouble)))
      }
    }
    val treeTest = dataTest.map{line=>{
      val parts = line.split(",")
      LabeledPoint(parts(0).toDouble,Vectors.dense(parts(1).split(" ").map(_.toDouble)))
    }}

    //赋值
    val (trainingData,testData) = (treeTrain,treeTest)

      //分类
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int,Int]()
    //分类为gini 、entorpy，回归为variance
    //对于分类问题，我们可以用熵entropy或Gini来表示信息的无序程度
    //对于回归问题，我们用方差Variance来表示无序程度，方差越大，说明数据间差异越大
    val impurity = "gini"// 不纯度

    //最大深度
    val maxDepth = 5 // 树的最大深度
    //最大分支
    val maxBins = 32 // 离散化"连续特征"的最大划分数
    //模型训练
    val model = DecisionTree.trainClassifier(trainingData,numClasses,categoricalFeaturesInfo,impurity,maxDepth,maxBins)
//    DecisionTree.trainRegressor()
    val labelAndPreds = testData.map{point =>{
      val prediction = model.predict(point.features)
      (point.label,prediction)
      }
    }

    //测试纸与真实值的对比
    val print_predict = labelAndPreds.take(15)
    println("label"+"\t"+"prediction")
    for(i <- 0 to print_predict.length-1){
      println(print_predict(i)._1+"\t"+print_predict(i)._2)
    }
    //树的错误率
    val testErr = labelAndPreds.filter(r=>r._1 != r._2).count.toDouble/testData.count()
    println("Test Error = "+testErr)
    //打印树的判断值
    println("Learned classification tree mode:\n"+model.toDebugString)

  }
}
