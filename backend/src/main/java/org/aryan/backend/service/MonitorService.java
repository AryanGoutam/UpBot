
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

@Service
public class MonitorService {

    private final WebsiteRepo websiteRepo;
    private final MonitorRepo monitorRepo;
    private final RestClient restClient;
    private final MonitorCacheService monitorCacheService;

    public MonitorService(
            WebsiteRepo websiteRepo,
            MonitorRepo monitorRepo,
            RestClient restClient,
            MonitorCacheService monitorCacheService
    ) {
        this.websiteRepo = websiteRepo;
        this.monitorRepo = monitorRepo;
        this.restClient = restClient;
        this.monitorCacheService = monitorCacheService;
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

    public List<MonitorResultDto> getMonitorResult(Long websiteId) {

        Websites website = websiteRepo.findById(websiteId)
                .orElseThrow();

        List<Monitor> monitorsList =
                monitorRepo.findByWebsite(website);

        return monitorsList.stream()
                .map(result -> new MonitorResultDto(
                        result.getCheckedAt(),
                        result.isUp(),
                        result.getStatusCode(),
                        result.getResponseTime(),
                        result.getErrorMessage()
                ))
                .toList();
    }
}
