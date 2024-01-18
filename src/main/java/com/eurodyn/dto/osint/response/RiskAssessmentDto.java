package com.eurodyn.dto.osint.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RiskAssessmentDto {
    private double confidence;
    private String crimeTopic;
    private double value;
}
