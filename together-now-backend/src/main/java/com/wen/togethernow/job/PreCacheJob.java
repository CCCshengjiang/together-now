package com.wen.togethernow.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.togethernow.mapper.UserMapper;
import com.wen.togethernow.model.domain.User;
import com.wen.togethernow.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
    private RedisTemplate<String, Object> redisTemplate;

    // TODO 动态设置重点用户
    private final List<Long> mainUsersId = List.of(2L);

    /**
     * 每天凌晨两点执行，预热推荐用户
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void doPreCacheRecommend() {
        for (Long userId : mainUsersId) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
            String redisKey = String.format("togethernow:user:recommend:%s", userId);

            // 将查询到的数据写到缓存，设置过期时间为：12h
            try {
                redisTemplate.opsForValue().set(redisKey, userPage, 12, TimeUnit.HOURS);
            } catch (Exception e) {
                log.error("Redis set key error", e);
            }
        }
    }

}