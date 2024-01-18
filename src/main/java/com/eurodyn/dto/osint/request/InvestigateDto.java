package com.eurodyn.dto.osint.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvestigateDto {
    String requestId;
    Instant timestamp;
    PersonDto person;
    List<String> crimeTopic;

    String risk_assesment_result_id;
    String riskAssessmentName;
    String riskAssessmentOwnerId;

}
