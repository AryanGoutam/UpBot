package org.aryan.backend.controller;


import org.aryan.backend.model.dto.DashBoardDTO;
import org.aryan.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashBoardDTO getDashboard(Authentication authentication){

            String email = authentication.getName();
            return dashboardService.getDashboard(email);

    }


}
