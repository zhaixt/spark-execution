package com.zhaixt.scala_study

/**
  * Created by zhaixt on 2017/7/13.
  */
class University {
  val id = University.newStudentNo
  private var number = 0
  def aClass (number : Int){this.number += number}
}
object University {
  private var studentNo = 0
  def newStudentNo = {
    studentNo += 1
    studentNo
  }
}