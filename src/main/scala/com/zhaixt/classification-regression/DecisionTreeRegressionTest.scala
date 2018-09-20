package com.zhaixt.classification_regression

import breeze.linalg.sum
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by zhaixiaotong on 2017-5-23.
  * http://blog.csdn.net/u010824591/article/details/50374904
  */
object DecisionTreeRegressionTest {
   def main(args: Array[String]) {

     val conf = new SparkConf().setAppName("KMeansTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data\\warehouse_dir")

     val sc = new SparkContext(conf)

     val records = sc.textFile("D:\\ProgrammingStudy\\spark-data\\Bike-Sharing-Dataset\\hour_noheader.csv").map(_.split(",")).cache()

       val mappings=for(i<-Range(2,10))yield get_mapping(records,i)

       val cat_len=sum(mappings.map(_.size))
       val num_len=records.first().slice(10,14).size
       val total_len=cat_len+num_len
       //decision tree data
       val data=records.map{record=>
         val features=record.slice(2,14).map(_.toDouble)
         val label=record(record.size-1).toDouble
         LabeledPoint(label,Vectors.dense(features))

       }

       val categoricalFeaturesInfo = Map[Int, Int]()
       val tree_model=DecisionTree.trainRegressor(data,categoricalFeaturesInfo,"variance",5,32)
       //    val linear_model=LinearRegressionWithSGD.train(data,10,0.5)
       val true_vs_predicted=data.map(p=>(p.label,tree_model.predict(p.features)))
       println( true_vs_predicted.take(5).toVector.toString())

     }

     def get_mapping(rdd:RDD[Array[String]], idx:Int)=
     {
       rdd.map(filed=>filed(idx)).distinct().zipWithIndex().collectAsMap()
     }
 }
