
package org.aryan.backend.service;

import org.aryan.backend.model.Monitor;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.MonitorResultDto;
import org.aryan.backend.repo.MonitorRepo;
import org.aryan.backend.repo.WebsiteRepo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

@Service
public class MonitorService {

    private final WebsiteRepo websiteRepo;
    private final MonitorRepo monitorRepo;
    private final RestClient restClient;
    private final MonitorCacheService monitorCacheService;
    private final ObjectMapper objectMapper;

    public MonitorService(
            WebsiteRepo websiteRepo,
            MonitorRepo monitorRepo,
            RestClient restClient,
            MonitorCacheService monitorCacheService,
            ObjectMapper objectMapper
    ) {
        this.websiteRepo = websiteRepo;
        this.monitorRepo = monitorRepo;
        this.restClient = restClient;
        this.monitorCacheService = monitorCacheService;
        this.objectMapper = objectMapper;
    }

    public void checkWebsite(Websites websites) {

        long startTime = System.currentTimeMillis();

        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(websites.getUrl())
                    .retrieve()
                    .toEntity(String.class);

            long responseTime =
                    System.currentTimeMillis() - startTime;

            Monitor result = new Monitor();

            result.setWebsite(websites);
            result.setCheckedAt(LocalDateTime.now());
            result.setUp(
                    response.getStatusCode().is2xxSuccessful()
            );
            result.setStatusCode(
                    response.getStatusCode().value()
            );
            result.setResponseTime(responseTime);

            // Save monitoring history to PostgreSQL
            Monitor savedResult = monitorRepo.save(result);

            // Cache the latest result in Redis
            monitorCacheService.cacheLatestResult(
                    websites.getId(),
                    savedResult.isUp(),
                    savedResult.getStatusCode(),
                    savedResult.getResponseTime(),
                    savedResult.getCheckedAt(),
                    savedResult.getErrorMessage()
            );

        } catch (RuntimeException e) {

            long responseTime =
                    System.currentTimeMillis() - startTime;

            Monitor result = new Monitor();

            result.setWebsite(websites);
            result.setCheckedAt(LocalDateTime.now());
            result.setUp(false);
            result.setStatusCode(null);
            result.setResponseTime(responseTime);
            result.setErrorMessage(e.getMessage());

            // Save failed check to PostgreSQL
            Monitor savedResult = monitorRepo.save(result);

            // Cache the failed result in Redis
            monitorCacheService.cacheLatestResult(
                    websites.getId(),
                    savedResult.isUp(),
                    savedResult.getStatusCode(),
                    savedResult.getResponseTime(),
                    savedResult.getCheckedAt(),
                    savedResult.getErrorMessage()
            );
        }
    }

    private MonitorResultDto toMonitorResultDto(String json) {
        try {
            Map<String, String> result = objectMapper.readValue(
                    json,
                    new TypeReference<Map<String, String>>() {}
            );

            String statusCode = result.get("statusCode");

            return new MonitorResultDto(
                    LocalDateTime.parse(result.get("checkedAt")),
                    Boolean.parseBoolean(result.get("isUp")),
                    statusCode == null || statusCode.isBlank()
                            ? null : Integer.parseInt(statusCode),
                    Long.parseLong(result.get("responseTime")),
                    result.get("errorMessage")
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to parse cached monitor result", e
            );
        }
    }


    public List<MonitorResultDto> getMonitorResult(Long websiteId) {

        Websites website = websiteRepo.findById(websiteId)
                .orElseThrow();

        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        // If Redis history is initialized, read from cache first.
        try {
            if (monitorCacheService.isHistoryInitialized(websiteId)) {

                Set<String> cachedResults =
                        monitorCacheService.getLast24HoursResults(websiteId);

                if (cachedResults != null && !cachedResults.isEmpty()) {
                    return cachedResults.stream()
                            .map(this::toMonitorResultDto)
                            .sorted(java.util.Comparator.comparing(
                                    MonitorResultDto::checkedAt
                            ))
                            .toList();
                }
            }
        } catch (Exception e) {
            System.err.println(
                    "Redis history read failed for website "
                            + websiteId + ": " + e.getMessage()
            );
        }

        // Cache miss: load the last 24 hours from PostgreSQL.
        List<Monitor> monitorsList =
                monitorRepo.findByWebsiteAndCheckedAtAfterOrderByCheckedAtAsc(
                        website, cutoff
                );

        List<MonitorResultDto> results = monitorsList.stream()
                .map(result -> new MonitorResultDto(
                        result.getCheckedAt(),
                        result.isUp(),
                        result.getStatusCode(),
                        result.getResponseTime(),
                        result.getErrorMessage()
                ))
                .toList();

        // Warm up Redis. A Redis failure must not prevent the DB response.
        try {
            for (Monitor monitor : monitorsList) {
                monitorCacheService.cacheHistoricalResult(
                        websiteId,
                        monitor.isUp(),
                        monitor.getStatusCode(),
                        monitor.getResponseTime(),
                        monitor.getCheckedAt(),
                        monitor.getErrorMessage()
                );
            }

            // Populate the latest-result hash with the newest DB record.
            if (!monitorsList.isEmpty()) {
                Monitor latest = monitorsList.get(monitorsList.size() - 1);

                monitorCacheService.cacheLatestResult(
                        websiteId,
                        latest.isUp(),
                        latest.getStatusCode(),
                        latest.getResponseTime(),
                        latest.getCheckedAt(),
                        latest.getErrorMessage()
                );
            }

            // Mark initialized only after the history load succeeds.
            monitorCacheService.markHistoryInitialized(websiteId);

        } catch (Exception e) {
            System.err.println(
                    "Redis warm-up failed for website "
                            + websiteId + ": " + e.getMessage()
            );
        }

        return results;
    }



}
