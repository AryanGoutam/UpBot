package org.aryan.backend.service;

import org.aryan.backend.model.User;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.WebsiteRequest;
import org.aryan.backend.repo.UserRepo;
import org.aryan.backend.repo.WebsiteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebsiteService {

    @Autowired
    private WebsiteRepo websitesRepo;
    @Autowired
    private UserRepo userRepo;

    public Websites add(WebsiteRequest request) {
        User user = userRepo.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Websites website = new Websites();

        website.setUser(user);
        website.setName(request.name());
        website.setUrl(request.url());
        website.setStatus(request.status());
        website.setCheckInterval(request.check_interval());

        return websitesRepo.save(website);
    }

    public List<Websites> getAll() {

        return websitesRepo.findAll();
    }
}
