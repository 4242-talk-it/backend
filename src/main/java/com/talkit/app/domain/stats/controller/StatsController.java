package com.talkit.app.domain.stats.controller;

import com.talkit.app.domain.stats.dto.StatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @GetMapping("/recent")
    public ResponseEntity<List<StatResponse>> getRecentStats() {
        List<StatResponse> statsList=List.of (
                new StatResponse("이번주 대화","12회"),
                new StatResponse("키워드 성공","2/10"),
                new StatResponse("평균 대화 길이","4턴")
        );
        return ResponseEntity.ok(statsList);
    }
}
