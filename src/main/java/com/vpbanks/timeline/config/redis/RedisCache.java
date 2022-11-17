package com.vpbanks.timeline.config.redis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisCache {

    Object setKey(String key, Object value);

    Object getKey(String key);

    Long rightPush(String queue, Object value);

    Long leftPush(String queue, Object value);

    Object leftPop(String queue);

    Object rightPop(String queue);

    Boolean hasKey(String key);

    Long addKeyOpsForSet(String key, Object value);

    Long addKeyOpsForSetWithTimeLive(String key, Object value, int timeLive, TimeUnit unit);

    Boolean isMemberOpsForSet(String key, Object value);

    Boolean delete(String key);

    Set<Object> memberOpsForSet(String key);

    void addKeyOpsForHash(String K, String HK, Object V);

    void addAllKeyOpsForHash(String K, Map<Object, Object> M);

    Map<Object, Object> getKeyOpsForHash(String K);

    Boolean hasKeyOpsForHash(String K, String HK);
}
