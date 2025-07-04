package com.bjpowernode.other.object;

import org.openjdk.jol.info.ClassLayout;

public class ObjectSpace {
    public static void main(String[] args) {
        Object obj = new Object();

        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
    }
}
