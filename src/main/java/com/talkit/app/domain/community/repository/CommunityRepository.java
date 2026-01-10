package com.talkit.app.domain.community.repository;

import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT c FROM Community c ORDER BY c.createdAt DESC")
    Page<Community> findAllByOrderByCreatedAtDesc(Pageable pageable);

    default Page<Community> findByIsPublicTrueOrUserId(Long userId, Pageable pageable) {
        return findAllByOrderByCreatedAtDesc(pageable);
    }

    @Query("SELECT c FROM Community c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    Page<Community> findByUserId(@Param("userId") Long userId, Pageable pageable);

    int countByUser(User user);

}
