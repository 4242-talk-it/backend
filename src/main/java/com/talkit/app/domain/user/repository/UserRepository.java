package com.talkit.app.domain.user.repository;

import com.talkit.app.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmailAndDeleteAtIsNull(String email);

    List<User> findAllByDeleteAtBefore(LocalDate date);

    Optional<User> findByEmail(String email);
}