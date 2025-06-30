package com.bjpowernode.other.dcl;

/**
 * 懒汉式单例
 * DCL   double check lock 双重检验锁
 */
public class SingletonLazy {

    //防止指令重排序
    private static volatile SingletonLazy instance;

    private SingletonLazy() {
    }

    public static SingletonLazy getInstance() {

        if (instance == null){
            //线程1  线程2
            synchronized (SingletonLazy.class){
                if (instance == null) {

                    instance = new SingletonLazy();
                }
            }
        }

        return instance;
    }
}
