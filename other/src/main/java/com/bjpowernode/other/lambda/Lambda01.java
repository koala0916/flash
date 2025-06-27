package com.bjpowernode.other.lambda;

import java.util.ArrayList;

public class Lambda01 {
    public static void main(String[] args) {
        //这里的()是与函数式接口中的抽象方法参数一致
//        MyInterface mi = () -> {
//            System.out.println("hello lambda");
//        };

       /* MyInterface mi = (String food) -> {
            System.out.println("hello lambda" + food);
        };*/

        //lambda的参数类型可以省略
        /*MyInterface mi = (food) -> {
            System.out.println("hello lambda" + food);
        };*/

        //只有1个参数的时候（）可以省略
       /* MyInterface mi = food -> {
            System.out.println("hello lambda" + food);
        };*/

        //MyInterface mi = (food, count) -> System.out.println("hello lambda");

        MyInterface mi = (food, count) ->  1;

        int result = mi.eat("苹果", 2);
        System.out.println(result);



    }
}
