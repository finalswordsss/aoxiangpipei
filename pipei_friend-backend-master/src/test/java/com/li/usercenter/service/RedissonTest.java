package com.li.usercenter.service;


import com.li.usercenter.job.PreCacheJob;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void redisTest(){
        //List
        List<String> list = new ArrayList<>();
        list.add("jack");
        System.out.println("list= " + list.get(0));

        //数据存在 redis 的内存中
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("jack");
        System.out.println("rList= " + rList.get(0));
    }


}
