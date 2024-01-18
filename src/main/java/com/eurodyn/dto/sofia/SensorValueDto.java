package com.eurodyn.dto.sofia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author vuktopalovic
 * @created 23/08/2023 - 11:22
 * @project sofia-plugin
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SensorValueDto {
    private double value;
    private String sensorId;
}
