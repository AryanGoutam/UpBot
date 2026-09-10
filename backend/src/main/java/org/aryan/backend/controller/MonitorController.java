package org.aryan.backend.controller;

import org.aryan.backend.model.dto.MonitorResultDto;
import org.aryan.backend.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/websites/{website_id}/monitor-result")
    public List<MonitorResultDto> getMonitorResult(@PathVariable Long website_id){
        return monitorService.getMonitorResult(website_id);
    }

}
