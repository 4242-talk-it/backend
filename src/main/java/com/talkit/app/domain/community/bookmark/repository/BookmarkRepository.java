package com.talkit.app.domain.community.bookmark.repository;

import com.talkit.app.domain.community.entity.Bookmark;
import com.talkit.app.domain.community.entity.Community;
import com.talkit.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndCommunity(User user, Community community);

    List<Bookmark> findAllByUser(User user);

    boolean existsByCommunityIdAndUserId(Long communityId, Long userId);

}
