package com.talkit.app.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTemperatureHistoryDto {
    private String month;
    private double temperature;
}
