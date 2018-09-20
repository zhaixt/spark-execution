package com.zhaixt

/**
  * Created by zhaixt on 2017/8/31.
  */
class ScalaClass(val year:Int) {
  private[this] var miles : Int = 0

  def drive(distance:Int) {miles += distance}

  override def toString():String = "year: " + year + " miles: " + miles
}
