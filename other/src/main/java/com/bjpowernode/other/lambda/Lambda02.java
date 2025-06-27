package com.bjpowernode.other.lambda;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;

public class Lambda02 {
    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();

        list.add("jack");
        list.add("paul");
        list.add("andy");

        /*
            ArrayList for int i
            LinkedList 增强for 迭代器

            forEach + lambda  自动选择最优的遍历方式  原理：方法重写

         */

        Consumer<String> consumer = name -> {
            System.out.println(name);
        };

        list.forEach(consumer);




    }
}
