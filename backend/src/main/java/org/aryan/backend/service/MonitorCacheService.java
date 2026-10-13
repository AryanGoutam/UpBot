
package org.aryan.backend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class MonitorCacheService {

    private final StringRedisTemplate redisTemplate;

    public MonitorCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheLatestResult(
            Long websiteId,
            boolean isUp,
            Integer statusCode,
            long responseTime,
            LocalDateTime checkedAt,
            String errorMessage
    ) {
        String key = "upbot:monitor:" + websiteId + ":latest";

        Map<String, String> result = new HashMap<>();

        result.put("isUp", String.valueOf(isUp));
        result.put("statusCode",
                statusCode == null ? "" : statusCode.toString());
        result.put("responseTime", String.valueOf(responseTime));
        result.put("checkedAt", checkedAt.toString());
        result.put("errorMessage",
                errorMessage == null ? "" : errorMessage);

        try {
            redisTemplate.opsForHash().putAll(key, result);

            // Keep cached data for 5 minutes
            redisTemplate.expire(key, Duration.ofMinutes(5));

        } catch (Exception e) {
            // Redis failure should not stop website monitoring
            System.err.println(
                    "Failed to cache monitor result for website "
                            + websiteId + ": " + e.getMessage()
            );
        }
    }

    public Map<Object, Object> getLatestResult(Long websiteId) {
        String key = "upbot:monitor:" + websiteId + ":latest";

        try {
            Map<Object, Object> result =
                    redisTemplate.opsForHash().entries(key);

            if (result == null || result.isEmpty()) {
                return Collections.emptyMap();
            }

            return result;

        } catch (Exception e) {
            System.err.println(
                    "Failed to read monitor result from Redis for website "
                            + websiteId + ": " + e.getMessage()
            );

            return Collections.emptyMap();
        }
    }
}
