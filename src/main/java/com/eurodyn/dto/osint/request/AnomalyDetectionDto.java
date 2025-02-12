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
public class AnomalyDetectionDto {
    Object query;
    Integer n;
    String minDate;
    String maxDate;
    String anomalyTypes2;
    String bookingRef;
    String flightNumber;
}
