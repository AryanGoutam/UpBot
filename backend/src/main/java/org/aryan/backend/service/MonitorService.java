package org.aryan.backend.service;

import org.aryan.backend.config.RestClientConfig;
import org.aryan.backend.model.Monitor;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.MonitorResultDto;
import org.aryan.backend.repo.MonitorRepo;
import org.aryan.backend.repo.WebsiteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitorService {

    @Autowired
    private WebsiteRepo websiteRepo;

    @Autowired
    private MonitorRepo  monitorRepo;

    private final RestClient restClient;

    // constructor to handle restClient
    public MonitorService(RestClient restClient){
        this.restClient = restClient;
    }


    public void checkWebsite(Websites websites){
        long startTime = System.currentTimeMillis();

        try{
            //Sending Http req
            ResponseEntity<String> response = restClient.get()
                    .uri(websites.getUrl())
                    .retrieve()
                    .toEntity(String.class);

            long responseTime =System.currentTimeMillis() - startTime;

            // add it into the monitor db
            Monitor result = new Monitor();

            result.setWebsite(websites);
            result.setChecked_at(LocalDateTime.now());
            result.setUp(response.getStatusCode().is2xxSuccessful());
            result.setStatus_code(response.getStatusCode().value());
            result.setResponse_time(responseTime);

            monitorRepo.save(result);

        } catch (RuntimeException e) {

            long responseTime = System.currentTimeMillis() - startTime;
            Monitor result = new Monitor();

            result.setWebsite(websites);
            result.setChecked_at(LocalDateTime.now());
            result.setUp(false);
            result.setStatus_code(null);
            result.setResponse_time(responseTime);
            result.setError_message(e.getMessage());

            monitorRepo.save(result);

        }
    }

    public List<MonitorResultDto> getMonitorResult(Long websiteId) {

        Websites website = websiteRepo.findById(websiteId).orElseThrow();

        List<Monitor> monitorsList = monitorRepo.findByWebsite(website);

        return monitorsList.stream()
                .map(result -> new MonitorResultDto(
                        result.getChecked_at(),
                        result.isUp(),
                        result.getStatus_code(),
                        result.getResponse_time(),
                        result.getError_message()
                )).toList();



    }
}
