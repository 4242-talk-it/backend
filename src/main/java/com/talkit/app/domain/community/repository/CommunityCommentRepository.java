package com.talkit.app.domain.community.repository;

import com.talkit.app.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends JpaRepository<Comment, Long> {

}
