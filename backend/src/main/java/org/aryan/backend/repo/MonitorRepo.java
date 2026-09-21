package org.aryan.backend.repo;

import org.aryan.backend.model.Monitor;
import org.aryan.backend.model.Websites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonitorRepo  extends JpaRepository<Monitor, Long> {
    List<Monitor> findByWebsite(Websites website);

    List<Monitor> findByWebsiteAndCheckedAtAfter(Websites website, LocalDateTime last24h);

    Monitor findTopByWebsiteOrderByCheckedAtDesc(Websites website);
}
