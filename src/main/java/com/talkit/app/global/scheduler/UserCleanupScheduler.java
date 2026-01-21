package com.talkit.app.global.scheduler;

import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {
    private final UserRepository userRepository;

    // 매일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredUsers() {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<User> expiredUsers = userRepository.findAllByDeleteAtBefore(oneMonthAgo);

        if (!expiredUsers.isEmpty()) {
            log.info("정기 삭제 스케줄러: {}명의 탈퇴 유저 영구 삭제 시작", expiredUsers.size());
            userRepository.deleteAll(expiredUsers);
            log.info("영구 삭제 완료");
        }
    }

}
