package com.talkit.app.domain.community.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.talkit.app.domain.community.dto.CommunitySearchConditionDto;
import com.talkit.app.domain.community.entity.Community;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static com.talkit.app.domain.community.entity.QCommunity.community;
import static com.talkit.app.domain.user.entity.QUser.user;

import java.util.List;

@RequiredArgsConstructor
public class CommunityRepositoryImpl implements CommunityRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Community> searchByCondition(CommunitySearchConditionDto condition, Pageable pageable) {
        List<Community> content = queryFactory
                .selectFrom(community)
                .leftJoin(community.user, user).fetchJoin()
                .leftJoin(community.tags).fetchJoin()
                .where(searchEq(condition.searchType(), condition.keyword()))
                .orderBy(community.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(community.count())
                .from(community)
                .where(searchEq(condition.searchType(), condition.keyword()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression searchEq(String searchType, String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        return switch (searchType) {
            case "TITLE" -> community.title.containsIgnoreCase(keyword);
            case "CONTENT" -> community.content.containsIgnoreCase(keyword);
            case "TAG" -> community.tags.any().containsIgnoreCase(keyword);
            case "WRITER" -> community.user.nickname.containsIgnoreCase(keyword);
            default -> null;
        };
    }
}
