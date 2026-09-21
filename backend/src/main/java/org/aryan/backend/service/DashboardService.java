package org.aryan.backend.service;

import org.aryan.backend.model.Monitor;
import org.aryan.backend.model.User;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.DashBoardDTO;
import org.aryan.backend.model.dto.MonitorResponseDTO;
import org.aryan.backend.repo.MonitorRepo;
import org.aryan.backend.repo.UserRepo;
import org.aryan.backend.repo.WebsiteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@CrossOrigin
public class DashboardService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebsiteRepo websiteRepo;
    @Autowired
    private MonitorRepo monitorRepo;


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
        if(user == null){
            throw new RuntimeException("User Not found");

        }

        List<Websites> websites = websiteRepo.findByUser(user);

        int total = websites.size();
        int active = 0;
        int operational = 0;
        int degraded = 0;
        int storm = 0;

        double totalUpChecks = 0;
        double totalChecks = 0;

        List<MonitorResponseDTO>monitorResponse = new ArrayList<>();
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);

        for(Websites website: websites){
            List<Monitor>checks = monitorRepo.findByWebsiteAndCheckedAtAfter(website, last24h);

            Monitor latest = monitorRepo.findTopByWebsiteOrderByCheckedAtDesc(website);

            if(latest == null) continue;
            active++;

            // uptime
            long upChecks = checks.stream().filter(Monitor::isUp).count();
            long totalWebsiteCheck = checks.size();
            double uptime24h = 0;
            if(totalWebsiteCheck > 0){
                uptime24h = ((double) upChecks/totalWebsiteCheck  )*100;
            }

            totalUpChecks += upChecks;
            totalChecks += totalWebsiteCheck;

            // current status

            String status;

            if (!latest.isUp()) {

                status = "DOWN";
                storm++;

            } else if (
                    latest.getResponseTime() != null &&
                            latest.getResponseTime() > 500
            ) {

                status = "DEGRADED";
                degraded++;

            } else {

                status = "OPERATIONAL";
                operational++;
            }

            // score aand score status
            int score = calculateScore(
                    uptime24h,
                    latest.getResponseTime(),
                    latest.isUp()
            );

            String scoreStatus =
                    getScoreStatus(score);



            //history
            List<Double> history = new ArrayList<>();

            for (Monitor check : checks) {

                if (check.isUp()) {
                    history.add(100.0);
                } else {
                    history.add(0.0);
                }
            }

            MonitorResponseDTO response = new MonitorResponseDTO(
                    website.getId(),
                    website.getName(),
                    website.getUrl(),
                    status,
                    latest.getResponseTime(),
                    uptime24h,
                    website.getCheckInterval(),
                    score,
                    scoreStatus,
                    history
            );
            monitorResponse.add(response);
        }

        // overall uptime
        double overallUptime = 0;
        if(totalChecks > 0){
            overallUptime = ((double) totalChecks/totalUpChecks) ;
        }

        String degradedMessage;
        if(degraded > 0){
            degradedMessage = "Slight response time lag";
        }
        else{
            degradedMessage = "No degraded monitors";
        }

        String stormMessage;
        if(storm>0){
            stormMessage = "Active outage reported";

        }
        else{
            stormMessage = "No active outage";
        }

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
