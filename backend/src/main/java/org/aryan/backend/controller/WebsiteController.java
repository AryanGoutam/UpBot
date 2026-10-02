package org.aryan.backend.controller;

import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.WebsiteRequest;
import org.aryan.backend.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;


    @PostMapping("/website")
    public Websites addWebsite(@RequestBody WebsiteRequest website, Authentication authentication){
        String email = authentication.getName();
        return websiteService.add(website,email);
    }

    @GetMapping("/websites")
    public List<Websites> getWebsites(){
        return websiteService.getAll();
    }

    @PutMapping("/website/{websiteId}/status")
    public Websites updateStatus(
            @PathVariable Long websiteId,
            @RequestBody Map<String, String> request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        System.out.println("WEBSITE ID = " + websiteId);
        System.out.println("NEW STATUS = " + request.get("status"));
        System.out.println("USER = " + email);

        return websiteService.updateStatus(
                websiteId,
                request.get("status"),
                email
        );
    }

    @DeleteMapping("/website/{websiteId}")
    public void deleteWebsite(
            @PathVariable Long websiteId,
            Authentication authentication
    ) {
        String email = authentication.getName();

        websiteService.deleteWebsite(websiteId, email);
    }


}
