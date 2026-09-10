package org.aryan.backend.controller;

import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.WebsiteRequest;
import org.aryan.backend.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;


    @PostMapping("/website")
    public Websites addWebsite(@RequestBody WebsiteRequest website){
        return websiteService.add(website);
    }

    @GetMapping("/websites")
    public List<Websites> getWebsites(){
        return websiteService.getAll();
    }


}
