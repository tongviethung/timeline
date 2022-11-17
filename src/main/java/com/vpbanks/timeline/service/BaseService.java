package com.vpbanks.timeline.service;

import com.vpbanks.timeline.config.redis.RedisCache;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.repository.ValidateRuleRepository;
import com.vpbanks.timeline.repository.entity.JobMonitorEntity;
import com.vpbanks.timeline.repository.entity.ValidateRuleEntity;
import com.vpbanks.timeline.response.ResponseDto;
import com.vpbanks.timeline.util.JsonUtil;
import com.vpbanks.timeline.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class BaseService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ValidateRuleRepository validateRuleRepository;

    public long getPage(long count, long pageSize) {
        long x = count % pageSize;
        if (x > 0) {
            return count / pageSize + 1;
        }
        return count / pageSize;
    }

    public void initRedisCache() {
        redisCache.delete(BaseConfigConstant.RedisKeyEnum.TIMELINE_EVENT_TYPE_VALID.getValue());
        log.info("DELETE KEY SUCCESS!!!");
        List<ValidateRuleEntity> validateRuleEntities = validateRuleRepository.findByTypeGroup(BaseConfigConstant.TypeGroupEnum.EVENT_TYPE.getValue());
        Map<Object, Object> map = new HashMap<>();

        for(ValidateRuleEntity val : validateRuleEntities){
            map.put(val.getTypeId(), JsonUtil.toJsonString(val));
        }
        redisCache.addAllKeyOpsForHash(BaseConfigConstant.RedisKeyEnum.TIMELINE_EVENT_TYPE_VALID.getValue(), map);
        log.info("PUT KEY SUCCESS!!!");
    }

    public Map<Object, Object> getCache() {
        Map<Object, Object> map = redisCache.getKeyOpsForHash(BaseConfigConstant.RedisKeyEnum.TIMELINE_EVENT_TYPE_VALID.getValue());
        return map;
    }
}
