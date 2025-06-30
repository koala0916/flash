package com.powernode;

import com.powernode.common.config.redisson.RedissonConfig;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootTest
public class ServiceOrderApplicationTest {

    @Resource
    private RedissonClient redissonClient;


    @Test
    public void testRedisson() {
        //要获取锁
        RLock learnRedisson = redissonClient.getLock("learnRedisson");


        //尝试加锁
        learnRedisson.lock();

        //若能执行到这里，则说明上加锁成功了   线程获取到锁
        System.out.println("加锁成功，可以执行业务逻辑了");

        //释放锁
        learnRedisson.unlock();
    }
}
