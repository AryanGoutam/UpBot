package org.aryan.backend.service;

import org.aryan.backend.model.Monitor;
import org.aryan.backend.model.User;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.DashBoardDTO;
import org.aryan.backend.model.dto.MonitorResponseDTO;
import org.aryan.backend.model.dto.MonitorResultDto;
import org.aryan.backend.repo.UserRepo;
import org.aryan.backend.repo.WebsiteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@CrossOrigin
public class DashboardService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebsiteRepo websiteRepo;
    @Autowired
    private MonitorService monitorService;




    private int calculateScore(
            double uptime,
            Long responseTime,
            boolean isUp
    ) {

        if (!isUp) {
            return 0;
        }

        // Uptime contributes 70%
        double uptimeScore = uptime * 0.70;

        // Latency contributes 30%
        double latencyScore;

        if (responseTime == null) {
            latencyScore = 0;

        } else if (responseTime <= 200) {
            latencyScore = 100;

        } else if (responseTime <= 500) {
            latencyScore = 80;

        } else if (responseTime <= 1000) {
            latencyScore = 60;

        } else if (responseTime <= 2000) {
            latencyScore = 40;

        } else {
            latencyScore = 20;
        }

        double score =
                uptimeScore +
                        (latencyScore * 0.30);

        return (int) Math.round(score);
    }


    private String getScoreStatus(int score) {

        if (score >= 90) {
            return "EXCELLENT";
        }

        if (score >= 75) {
            return "GOOD";
        }

        if (score >= 50) {
            return "WARNING";
        }

        return "POOR";
    }


    public DashBoardDTO getDashboard(String email) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Websites> websites = websiteRepo.findByUser(user);

        int total = websites.size();
        int active = 0;
        int operational = 0;
        int degraded = 0;
        int storm = 0;

        double totalUpChecks = 0;
        double totalChecks = 0;

        List<MonitorResponseDTO> monitorResponse = new ArrayList<>();

        LocalDateTime last24h = LocalDateTime.now().minusHours(24);


        for (Websites website : websites) {

            // MonitorService handles Redis/cache and database fallback.
            List<MonitorResultDto> checks =
                    monitorService.getMonitorResult(website.getId());

            boolean paused =
                    "PAUSED".equalsIgnoreCase(website.getStatus());

            // Latest check is the most recent check.
            List<MonitorResultDto> recentChecks = new ArrayList<>(checks);

            recentChecks.sort(
                    Comparator.comparing(MonitorResultDto::checkedAt).reversed()
            );

            MonitorResultDto latest =
                    recentChecks.isEmpty() ? null : recentChecks.get(0);

            // No monitoring result anywhere.
            if (latest == null) {

                String status = paused ? "PAUSED" : "PENDING";

                monitorResponse.add(new MonitorResponseDTO(
                        website.getId(),
                        website.getName(),
                        website.getUrl(),
                        status,
                        null,
                        0.0,
                        website.getCheckInterval(),
                        0,
                        "PENDING",
                        new ArrayList<>()
                ));

                continue;
            }

            if (!paused) {
                active++;
            }

            // Calculate uptime using the checks returned by MonitorService.
            long upChecks = checks.stream()
                    .filter(MonitorResultDto::up)
                    .count();

            long totalWebsiteChecks = checks.size();

            double uptime24h = 0.0;

            if (totalWebsiteChecks > 0) {
                uptime24h =
                        ((double) upChecks / totalWebsiteChecks) * 100.0;

                totalUpChecks += upChecks;
                totalChecks += totalWebsiteChecks;
            }

            // Determine current status from the last three checks.
            String status;

            if (paused) {

                status = "PAUSED";

            } else {

                int limit = Math.min(3, recentChecks.size());

                List<MonitorResultDto> lastChecks =
                        recentChecks.subList(0, limit);

                long failedChecks = lastChecks.stream()
                        .filter(check -> !check.up())
                        .count();

                long successfulChecks = lastChecks.size() - failedChecks;

                if (lastChecks.isEmpty()) {

                    status = "PENDING";

                } else if (failedChecks >= 2) {

                    status = "DOWN";
                    storm++;

                } else if (successfulChecks > 0) {

                    MonitorResultDto lastSuccessfulCheck = lastChecks.stream()
                            .filter(MonitorResultDto::up)
                            .findFirst()
                            .orElse(null);

                    if (lastSuccessfulCheck != null
                            && lastSuccessfulCheck.responseTime() != null
                            && lastSuccessfulCheck.responseTime() > 800) {

                        status = "DEGRADED";
                        degraded++;

                    } else {

                        status = "OPERATIONAL";
                        operational++;
                    }

                } else {

                    status = "PENDING";
                }
            }

            // Calculate score from the latest check.
            int score = paused
                    ? 0
                    : calculateScore(
                    uptime24h,
                    latest.responseTime(),
                    latest.up()
            );

            String scoreStatus = getScoreStatus(score);

            // Build uptime history.
            List<Double> history = new ArrayList<>();

            for (MonitorResultDto check : checks) {
                history.add(check.up() ? 100.0 : 0.0);
            }

            MonitorResponseDTO response = new MonitorResponseDTO(
                    website.getId(),
                    website.getName(),
                    website.getUrl(),
                    status,
                    latest.responseTime(),
                    uptime24h,
                    website.getCheckInterval(),
                    score,
                    scoreStatus,
                    history
            );

            monitorResponse.add(response);
        }


        // Overall uptime
        double overallUptime = 0.0;

        if (totalChecks > 0) {
            overallUptime =
                    (totalUpChecks / totalChecks) * 100.0;
        }

        String degradedMessage = degraded > 0
                ? "Slight response time lag"
                : "No degraded monitors";

        String stormMessage = storm > 0
                ? "Active outage reported"
                : "No active outage";

        return new DashBoardDTO(
                total,
                active,
                operational,
                degraded,
                storm,
                overallUptime,
                degradedMessage,
                stormMessage,
                monitorResponse
        );
    }




}
