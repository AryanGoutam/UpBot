package org.aryan.backend.repo;

import org.aryan.backend.model.User;
import org.aryan.backend.model.Websites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebsiteRepo extends JpaRepository<Websites,Long> {
    List<Websites> findByUser(User user);
}
