package com.vpbanks.timeline.service.validate;

import com.vpbanks.timeline.config.redis.RedisCache;
import com.vpbanks.timeline.constants.BaseConfigConstant;
import com.vpbanks.timeline.constants.ValidateResultConstant;
import com.vpbanks.timeline.repository.ValidateRuleRepository;
import com.vpbanks.timeline.repository.entity.FieldRule;
import com.vpbanks.timeline.repository.entity.ValidateRuleEntity;
import com.vpbanks.timeline.service.BaseService;
import com.vpbanks.timeline.service.ValidationEventService;
import com.vpbanks.timeline.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ValidationEventServiceImpl extends BaseService implements ValidationEventService {

    private final ValidateRuleRepository validateRuleRepository;
    private final RedisCache redisCache;

    @Override
    public List<ValidateResultConstant> validateCommonByEventType(String typeGroup, String typeId, Object obj) throws Exception {
        Map<Object, Object> mapCache = redisCache.getKeyOpsForHash(BaseConfigConstant.RedisKeyEnum.TIMELINE_EVENT_TYPE_VALID.getValue());
        if (mapCache.isEmpty()) {
            super.initRedisCache();
            mapCache = redisCache.getKeyOpsForHash(BaseConfigConstant.RedisKeyEnum.TIMELINE_EVENT_TYPE_VALID.getValue());
        }

        Object value = null;
        for (Object key : mapCache.keySet()) {
            if(String.valueOf(key).equals(typeId)){
                value = mapCache.get(key);
                break;
            }
        }
        //by pass
        if(Objects.isNull(value)) return null;

        ValidateRuleEntity validateRuleEntity = JsonUtil.getGenericObject(String.valueOf(value), ValidateRuleEntity.class);

        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        List<ValidateResultConstant> validateResults = new ArrayList<>();
        for (FieldRule v : validateRuleEntity.getFieldsRule()) {
            if (v.getIsRequired() != null && v.getIsRequired() == 1 && !hasField(v.getField(), map)) {
                validateResults.add(ValidateResultConstant.buildRequireFieldError(v.getField()));
            }
        }

        if(validateResults.isEmpty()){
            return null;
        }
        return validateResults;
    }

    private boolean hasField(String fieldName, Map<String, Object> data) {
        Object value = data.get(fieldName);
        return value != null && StringUtils.hasText(value.toString());
    }
}
