package com.talkit.app.domain.user.repository;

import com.talkit.app.domain.user.entity.UserTemperatureHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserTemperatureHistoryRepository extends JpaRepository<UserTemperatureHistory, Long> {
    @Query(value = "SELECT DATE_FORMAT(h.recorded_at, '%Y-%m') as month, ROUND(AVG(h.temperature), 1) as avgTemp " +
            "FROM user_temperature_history h " +
            "WHERE h.user_id = :userId AND h.recorded_at >= :startDate " +
            "GROUP BY month " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Map<String, Object>> findMonthlyAverageNative(@Param("userId") Long userId,
                                                       @Param("startDate") LocalDateTime startDate);
}