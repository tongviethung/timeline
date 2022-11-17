package com.vpbanks.timeline.config.redis;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCacheImpl implements RedisCache {

    Logger logger = LoggerFactory.getLogger(getClass());
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object setKey(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return value;
        } catch (Exception e) {
            logger.warn("Error in Redis: setKey", e);
        }
        return null;
    }

    @Override
    public Object getKey(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        }catch (Exception e){
            logger.warn("Error in Redis: getKey", e);
        }
        return null;
    }

    @Override
    public Long rightPush(String queue, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(queue, value);
        }catch (Exception e){
            logger.warn("Error in Redis: rightPush", e);
        }
        return null;
    }

    @Override
    public Long leftPush(String queue, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(queue, value);
        }catch (Exception e){
            logger.warn("Error in Redis: rightPush", e);
        }
        return null;
    }

    @Override
    public Object leftPop(String queue) {
        try {
            return redisTemplate.opsForList().leftPop(queue);
        }catch (Exception e){
            logger.warn("Error in Redis: leftPop", e);
        }
        return null;
    }

    @Override
    public Object rightPop(String queue) {
        try {
            return redisTemplate.opsForList().rightPop(queue);
        }catch (Exception e){
            logger.warn("Error in Redis: rightPop", e);
        }
        return null;
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        }catch (Exception e){
            logger.warn("Error in Redis: hasKey", e);
            return Boolean.FALSE;
        }
    }

    @Override
    public Long addKeyOpsForSet(String key, Object value) {
        try {
            return redisTemplate.opsForSet().add(key, value);
        }catch (Exception e){
            logger.warn("Error in Redis: addKeyOpsForSet", e);
            return null;
        }
    }

    @Override
    public Long addKeyOpsForSetWithTimeLive(String key, Object value, int timeLive, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeLive, unit);
            return redisTemplate.opsForSet().add(key, value);
        }catch (Exception e){
            logger.warn("Error in Redis: addKeyOpsForSet", e);
            return null;
        }
    }

    @Override
    public Boolean isMemberOpsForSet(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        }catch (Exception e){
            logger.warn("Error in Redis: addKeyOpsForSet: ", e);
            return false;
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        }catch (Exception e){
            logger.warn("Error in Redis: delete: ", e);
            return false;
        }
    }

    @Override
    public Set<Object> memberOpsForSet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        }catch (Exception e){
            logger.warn("Error in Redis: memberOpsForSet: ", e);
            return null;
        }
    }

    @Override
    public void addKeyOpsForHash(String K, String HK, Object V) {
        try {
            redisTemplate.opsForHash().put(K, HK, V);
        }catch (Exception e){
            logger.warn("Error in Redis: addKeyOpsForHash: ", e);
        }
    }

    @Override
    public void addAllKeyOpsForHash(String K, Map<Object, Object> M) {
        try {
            redisTemplate.opsForHash().putAll(K, M);
        }catch (Exception e){
            logger.warn("Error in Redis: addAllKeyOpsForHash: ", e);
        }
    }

    @Override
    public Map<Object, Object> getKeyOpsForHash(String K) {
        try {
            return redisTemplate.opsForHash().entries(K);
        }catch (Exception e){
            logger.warn("Error in Redis: getKeyOpsForHash: ", e);
            return null;
        }
    }

    @Override
    public Boolean hasKeyOpsForHash(String K, String HK) {
        try {
            return redisTemplate.opsForHash().hasKey(K, HK);
        }catch (Exception e){
            logger.warn("Error in Redis: getKeyOpsForHash: ", e);
            return null;
        }
    }
}
