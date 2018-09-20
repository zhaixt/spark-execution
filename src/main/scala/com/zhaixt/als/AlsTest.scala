package com.zhaixt.als

import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by zhaixiaotong on 2017-5-4.
 * http://www.cnblogs.com/jackchen-Net/p/6648274.html#_label4
 *  ALS最小二乘法
 */
object AlsTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CollaborationFilterTest").setMaster("local").set("spark.sql.warehouse.dir", "D:\\ProgrammingStudy\\spark-data")
    val sc = new SparkContext(conf)
    //第一列位用户编号，第二列位产品编号，第三列的评分Rating为Double类型
    val data = sc.textFile("D:\\ProgrammingStudy\\spark-data\\sample_als.txt")
    //处理数据
    val ratings=data.map(_.split(' ') match{
      //数据集的转换
      case Array(user,item,rate) =>
        //将数据集转化为专用的Rating
        Rating(user.toInt,item.toInt,rate.toDouble)
    })
    //设置隐藏因子
    /*
      对应ALS模型中的因子个数，也就是在低阶近似矩阵中的隐含特征个数。因子个数一般越多越好。
      但它也会直接影响模型训练和保存时所需的内存开销，尤其是在用户和物品很多的时候。
      因此实践中该参数常作为训练效果与系统开销之间的调节参数。通常，其合理取值为10到200。
    */
    val rank=2
    //设置迭代次数
    val numIterations=2
    //进行模型训练
    val model =ALS.train(ratings,rank,numIterations,0.01)
//    ALS.trainImplicit()
    //为用户2推荐一个商品
    val rs=model.recommendProducts(2,1)
    //打印结果
    rs.foreach(println)
  }
}
