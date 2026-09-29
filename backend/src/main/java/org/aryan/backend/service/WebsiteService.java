package org.aryan.backend.service;

import org.aryan.backend.model.User;
import org.aryan.backend.model.Websites;
import org.aryan.backend.model.dto.WebsiteRequest;
import org.aryan.backend.repo.MonitorRepo;
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
    @Autowired
    private MonitorRepo monitorRepo;

    public Websites add(WebsiteRequest request, String email) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

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

    public Websites updateStatus(Long websiteId, String status, String email) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Websites website = websitesRepo.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));

        if (!website.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        website.setStatus(status);

        return websitesRepo.save(website);
    }

    public void deleteWebsite(Long websiteId, String email) {

        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Websites website = websitesRepo.findById(websiteId)
                .orElseThrow(() -> new RuntimeException("Website not found"));

        if (!website.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        monitorRepo.deleteAll(monitorRepo.findByWebsite(website));

        websitesRepo.delete(website);
    }
}
