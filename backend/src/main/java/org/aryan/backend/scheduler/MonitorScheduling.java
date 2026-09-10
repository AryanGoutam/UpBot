package org.aryan.backend.scheduler;

import org.aryan.backend.model.Websites;
import org.aryan.backend.repo.MonitorRepo;
import org.aryan.backend.repo.WebsiteRepo;
import org.aryan.backend.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitorScheduling {

    @Autowired
    private WebsiteRepo websiteRepo;
    @Autowired
    private MonitorService monitorService;


    @Scheduled(fixedRate = 60000)
    public void mointorWebsite(){
        System.out.println("Scheduler is running");
        List<Websites> websites = websiteRepo.findAll();

        for(Websites website:websites){
            monitorService.checkWebsite(website);
        }
    }
}
