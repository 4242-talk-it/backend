package com.talkit.app.domain.chatting.badge.dto;

import com.talkit.app.domain.chatting.badge.entity.UserBadge;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BadgeResponse() {

    public record MyBadge(
            Long badgeId,
            String name,
            String description,
            String icon,
            LocalDateTime earnedAt
    ) {
        public static MyBadge from(UserBadge userBadge) {
            return new MyBadge(
                    userBadge.getBadge().getId(),
                    userBadge.getBadge().getName(),
                    userBadge.getBadge().getDescription(),
                    userBadge.getBadge().getIcon(),
                    userBadge.getCreatedAt()
            );
        }
    }
}
