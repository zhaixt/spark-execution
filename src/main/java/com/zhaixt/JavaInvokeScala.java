package com.zhaixt;

/**
 * Created by zhaixt on 2017/8/31.
 */
public class JavaInvokeScala {
    public static void main(String[] args){
        ScalaClass car = new ScalaClass(2014);
        System.out.println(car);
        car.drive(10);
        System.out.println(car);
    }
}
