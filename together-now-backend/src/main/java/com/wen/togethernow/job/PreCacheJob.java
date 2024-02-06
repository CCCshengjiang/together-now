package com.wen.togethernow.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 缓存预热任务
 *
 * @author wen
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 主要用户的 id 集合
     */
    private final List<Long> mainUsersId = LongStream.rangeClosed(0, 30)
            .boxed()
            .toList();

    /**
     * 每天凌晨两点执行，预热推荐用户
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void doPreCacheRecommend() {
        RLock lock = redissonClient.getLock("together:preCacheJob:doPreCacheRecommend:lock");
        try {
            // 只抢一次抢不到就放弃，锁的过期时间：5min
            if (lock.tryLock(0, 5, TimeUnit.MINUTES)) {
                List<User> userList = userService.list();
                String redisKey = "togethernow:user:recommend";
                // 将查询到的数据写到缓存，设置过期时间为：24h
                try {
                    redisTemplate.opsForValue().set(redisKey, userList, 24, TimeUnit.HOURS);
                } catch (Exception e) {
                    log.error("Redis set key error", e);
                }
            }
        } catch (InterruptedException e) {
            log.error("doPreCacheRecommend error", e);
        } finally {
            // 只能释放自己加的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 每天早上八点执行，预热用户匹配
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void doPreCacheMatch() {
        RLock lock = redissonClient.getLock("together:preCacheJob:doPreCacheMatch:lock");
        try {
            // 只抢一次抢不到就放弃，锁的过期时间：5min
            if (lock.tryLock(0, 5, TimeUnit.MINUTES)) {
                for (Long userId : mainUsersId) {
                    String redisKey = String.format("togethernow:user:match:%s", userId);
                    User user = userService.getById(userId);
                    // 用户注销或者没有用户
                    if (user == null) {
                        continue;
                    }
                    List<User> matchUsers = userService.getMatchUsers(user);
                    // 将查询到的数据写到缓存，设置过期时间为：12h
                    try {
                        redisTemplate.opsForValue().set(redisKey, matchUsers, 12, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("Redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doPreCacheMatch error", e);
        } finally {
            // 只能释放自己加的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}