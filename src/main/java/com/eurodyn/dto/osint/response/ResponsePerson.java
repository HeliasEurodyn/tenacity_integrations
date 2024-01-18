package com.eurodyn.dto.osint.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ResponsePerson {
    private List<RiskAssessmentDto> riskAssessment;
}
