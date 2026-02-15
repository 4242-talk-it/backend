package com.talkit.app.domain.community.like.repository;

import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.community.entity.CommunityLike;
import com.talkit.app.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    Optional<CommunityLike> findByUserAndCommunity(User user, Community community);

    @Query("SELECT clike FROM CommunityLike clike WHERE clike.user.id = :userId AND clike.community.id = :communityId")
    Optional<CommunityLike> findByUserIdAndCommunityId(@Param("userId") Long userId, @Param("communityId") Long communityId);

    List<CommunityLike> findCommunityLikesByUserId(Long userId);
    int countByCommunityId(Long communityId);
}
