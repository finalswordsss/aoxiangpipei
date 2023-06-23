package com.li.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.li.usercenter.mapper")
@EnableScheduling   //定时执行任务，因为once中只执行一次，而springboot中不可以写个main函数直接跑代码，故设置成只跑一次的注解
public class UserCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

}
