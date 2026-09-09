package org.aryan.backend.repo;

import org.aryan.backend.model.Websites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteRepo extends JpaRepository<Websites,Long> {
}
