package com.talkit.app.domain.user.repository;

import com.talkit.app.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmailAndDeleteAtIsNull(String email);

    List<User> findAllByDeleteAtBefore(LocalDate date);

    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u " +
            "WHERE EXISTS (SELECT 1 FROM Community c WHERE c.user = u) " +
            "OR EXISTS (SELECT 1 FROM Comment cc WHERE cc.user = u)")
    long countActiveMembers();
}