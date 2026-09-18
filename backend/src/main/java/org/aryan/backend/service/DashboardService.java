package org.aryan.backend.service;

import org.aryan.backend.model.User;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.DashBoardDTO;
import org.aryan.backend.model.dto.MonitorResponseDTO;
import org.aryan.backend.repo.MonitorRepo;
import org.aryan.backend.repo.UserRepo;
import org.aryan.backend.repo.WebsiteRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private WebsiteRepo websiteRepo;
    @Autowired
    private MonitorRepo monitorRepo;



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

        }

        return null;


    }
}
