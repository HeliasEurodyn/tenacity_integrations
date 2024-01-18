package com.eurodyn.dto.osint.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class InvestigationResponseDto {

    private ResponsePerson person;
    private String requestId;
    private Instant timestamp;
    private String done;
}
