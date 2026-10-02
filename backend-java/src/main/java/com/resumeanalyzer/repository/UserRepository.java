package com.resumeanalyzer.repository;

import com.resumeanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by normalized email (trimmed and lowercased).
     * Exact V1.0 Python equivalent:
     * SELECT name, email, password FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))
     */
    @Query("SELECT u FROM User u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    Optional<User> findByNormalizedEmail(@Param("email") String email);

    /**
     * Checks if a user exists by normalized email (trimmed and lowercased).
     * Exact V1.0 Python equivalent:
     * SELECT id FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    boolean existsByNormalizedEmail(@Param("email") String email);
}
