package com.talkit.app.domain.like.repository;

import com.talkit.app.domain.like.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
}
