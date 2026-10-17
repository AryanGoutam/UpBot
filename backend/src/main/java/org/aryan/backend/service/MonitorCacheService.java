
package org.aryan.backend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MonitorCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration LATEST_TTL =
            Duration.ofMinutes(20);

    private static final Duration HISTORY_TTL =
            Duration.ofHours(25);

    private final ObjectMapper objectMapper;
    public MonitorCacheService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private String latestKey(Long websiteId) {
        return "upbot:monitor:" + websiteId + ":latest";
    }

    private String historyKey(Long websiteId) {
        return "upbot:monitor:" + websiteId + ":history";
    }

    private String initializedKey(Long websiteId) {
        return "upbot:monitor:" + websiteId + ":initialized";
    }



    private Map<String, String> toMap(
            boolean isUp,
            Integer statusCode,
            long responseTime,
            LocalDateTime checkedAt,
            String errorMessage
    ) {
        Map<String, String> result = new HashMap<>();

        result.put("isUp", String.valueOf(isUp));
        result.put(
                "statusCode",
                statusCode == null ? "" : statusCode.toString()
        );
        result.put("responseTime", String.valueOf(responseTime));
        result.put("checkedAt", checkedAt.toString());
        result.put(
                "errorMessage",
                errorMessage == null ? "" : errorMessage
        );

        return result;
    }

    // Cache the latest result AND append it to the history.
    public void cacheLatestResult(
            Long websiteId,
            boolean isUp,
            Integer statusCode,
            long responseTime,
            LocalDateTime checkedAt,
            String errorMessage
    ) {
        String latestKey = latestKey(websiteId);
        String historyKey = historyKey(websiteId);

        try {
            Map<String, String> result = toMap(
                    isUp, statusCode, responseTime,
                    checkedAt, errorMessage
            );

            // Update latest result.
            redisTemplate.opsForHash().putAll(latestKey, result);
            redisTemplate.expire(latestKey, LATEST_TTL);

            // Store the complete result as a history entry JSon format
            String historyEntry = objectMapper.writeValueAsString(result);

            double timestamp = checkedAt
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli();

            redisTemplate.opsForZSet().add(
                    historyKey, historyEntry, timestamp
            );

            // Remove entries older than 24 hours.
            double cutoff = java.time.Instant.now()
                    .minus(java.time.Duration.ofHours(24))
                    .toEpochMilli();

            redisTemplate.opsForZSet().removeRangeByScore(
                    historyKey, 0, cutoff
            );

            redisTemplate.expire(historyKey, HISTORY_TTL);

        } catch (Exception e) {
            System.err.println(
                    "Failed to cache monitor result for website "
                            + websiteId + ": " + e.getMessage()
            );
        }
    }

    public Map<Object, Object> getLatestResult(Long websiteId) {
        try {
            Map<Object, Object> result =
                    redisTemplate.opsForHash()
                            .entries(latestKey(websiteId));

            return result == null
                    ? Collections.emptyMap()
                    : result;

        } catch (Exception e) {
            System.err.println(
                    "Failed to read latest result from Redis for website "
                            + websiteId + ": " + e.getMessage()
            );

            return Collections.emptyMap();
        }
    }

    // Has this monitor's history been loaded from the database?
    public boolean isHistoryInitialized(Long websiteId) {
        try {
            return Boolean.TRUE.equals(
                    redisTemplate.hasKey(initializedKey(websiteId))
            );
        } catch (Exception e) {
            return false;
        }
    }

    // Call only after the initial database history load succeeds.
    public void markHistoryInitialized(Long websiteId) {
        redisTemplate.opsForValue().set(
                initializedKey(websiteId),
                "true",
                HISTORY_TTL
        );
    }

    // Add historical records fetched from the database.
    // Call this during initial cache warm-up.
    public void cacheHistoricalResult(
            Long websiteId,
            boolean isUp,
            Integer statusCode,
            long responseTime,
            LocalDateTime checkedAt,
            String errorMessage
    ) {
        try {
            Map<String, String> result = toMap(
                    isUp, statusCode, responseTime,
                    checkedAt, errorMessage
            );

            String historyEntry = objectMapper.writeValueAsString(result);

            double timestamp = checkedAt
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli();

            redisTemplate.opsForZSet().add(
                    historyKey(websiteId), historyEntry, timestamp
            );

            redisTemplate.expire(historyKey(websiteId), HISTORY_TTL);

        } catch (Exception e) {
            System.err.println(
                    "Failed to cache historical result for website "
                            + websiteId + ": " + e.getMessage()
            );

        }
    }

    // Read all history entries from the last 24 hours.
    public Set<String> getLast24HoursResults(Long websiteId) {
        try {
            double cutoff = LocalDateTime.now()
                    .minusHours(24)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli();

            Set<String> results = redisTemplate.opsForZSet()
                    .rangeByScore(historyKey(websiteId), cutoff,
                            Double.MAX_VALUE);

            return results == null
                    ? Collections.emptySet()
                    : results;

        } catch (Exception e) {
            System.err.println(
                    "Failed to read history from Redis for website "
                            + websiteId + ": " + e.getMessage()
            );

            return Collections.emptySet();
        }
    }

    public void clearHistory(Long websiteId) {
        redisTemplate.delete(historyKey(websiteId));
        redisTemplate.delete(initializedKey(websiteId));
    }
}
