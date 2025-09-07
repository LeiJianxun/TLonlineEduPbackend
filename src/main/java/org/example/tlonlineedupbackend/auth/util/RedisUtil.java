package org.example.tlonlineedupbackend.auth.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 课程学习 Redis 前缀
    private static final String COURSE_LEARNING_PREFIX = "course_learning:";

    // 课程学习 Redis
    public void increment(String key) {
        String courseKey = COURSE_LEARNING_PREFIX + key;
        redisTemplate.opsForValue().increment(courseKey);
    }

    public void decrement(String key) {
        String courseKey = COURSE_LEARNING_PREFIX + key;
        redisTemplate.opsForValue().decrement(courseKey);
    }

    public Long getValue(String key) {
        String courseKey = COURSE_LEARNING_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(courseKey);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }
        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }

    // 在线人数 Redis 前缀
    private static final String ONLINE_USERS_PREFIX = "online_users:";
    private static final String ONLINE_USERS_SET_KEY = "online_users_set";

    public void incrementUser(String key) {
        String onlineKey = ONLINE_USERS_PREFIX + key;
        redisTemplate.opsForValue().increment(onlineKey, 1);
        redisTemplate.expire(onlineKey, 15, TimeUnit.SECONDS); // 设置过期时间
    }

    public void decrementUser(String key) {
        String onlineKey = ONLINE_USERS_PREFIX + key;
        Long value = redisTemplate.opsForValue().decrement(onlineKey, 1);
        if (value != null && value <= 0) {
            redisTemplate.delete(onlineKey);
        }
    }

    // 添加用户到在线集合（带过期时间）
    public void addOnlineUser(String userId) {
        redisTemplate.opsForSet().add(ONLINE_USERS_SET_KEY, userId);
        redisTemplate.expire(ONLINE_USERS_SET_KEY, 5, TimeUnit.MINUTES); // 统一过期时间
    }

    // 获取在线用户数
    public Long getOnlineCount() {
        return redisTemplate.opsForSet().size(ONLINE_USERS_SET_KEY);
    }

    // 移除离线用户（可选）
    public void removeOfflineUser(String userId) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_SET_KEY, userId);
    }

    public Long getValueUser(String key) {
        String onlineKey = ONLINE_USERS_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(onlineKey);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        }
        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
}