package com.eurodyn.dto.blockchain_api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AcknowledgeDTO {
    Map<String, Map<String, Object>> request;

    List<Map<String, Object>> passengerDatasets;
}
