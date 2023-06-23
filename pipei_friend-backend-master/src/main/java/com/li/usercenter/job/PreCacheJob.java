package com.li.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li.usercenter.model.domain.User;
import com.li.usercenter.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务,用于给第一次提速，可以用于双十一，或者指定用户提
 *
 * @author li
 *
 */
@Component
@Slf4j
//使用ression来管理，先加入依赖org.redisson，再写入配置
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;



    // 重点用户
    private List<Integer> mainUserList = Arrays.asList(1);

    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("li:precachejob:doache:lock");

        try {
            if(lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
            for (Integer userId : mainUserList) {
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                String redisKey = String.format("li:user:recommend:%s", userId);
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                        // 写缓存
                        try {
                            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            log.error("redis set key error", e);
                        }
                    }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
        }




