package com.bjpowernode.other.lambda;

/**
函数式编程
    add(1,2)
    subtract(add(1,2),3)
    multiply(subtract(add(1,2),3),4)

 命令式编程
    int a = 1 + 2;
    int b = a - 3;
    int c = b * 4;

  函数式接口
   一个接口有且仅有1个抽象方法

 lambda表达式：lambda只能用于函数式接口中
 */
@FunctionalInterface  //java用来检查当前接口是否为函数式接口
public interface MyInterface {
    int eat(String food, int count);
    default void sleep(){

    }
}
