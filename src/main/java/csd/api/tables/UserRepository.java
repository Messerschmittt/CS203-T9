package csd.api.tables;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    // define a derived query to find user by username
    ApplicationUser findByUsername(String username);
    Optional<ApplicationUser> findById(Integer Id);

    boolean existsByUsername(String username);
}