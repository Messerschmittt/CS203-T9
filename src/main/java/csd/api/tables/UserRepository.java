package csd.api.tables;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    // define a derived query to find user by username
    ApplicationUser findByUsername(String username);
}