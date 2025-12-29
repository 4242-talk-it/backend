package com.talkit.app.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatResponse {
    private String label;
    private String value;
}
