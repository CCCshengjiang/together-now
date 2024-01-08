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

    // TODO 动态设置重点用户
    private final List<Long> mainUsersId = List.of(2L);

    /**
     * 每天凌晨两点执行，预热推荐用户
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void doPreCacheRecommend() {
        RLock lock = redissonClient.getLock("together:precachejob:docache:lock");
        try {
            // 只抢一次抢不到就放弃，锁的过期时间：1min
            if (lock.tryLock(0, 60000, TimeUnit.MILLISECONDS)) {
                for (Long userId : mainUsersId) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("togethernow:user:recommend:%s", userId);

                    // 将查询到的数据写到缓存，设置过期时间为：2h
                    try {
                        redisTemplate.opsForValue().set(redisKey, userPage, 2, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("Redis set key error", e);
                    }
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

}