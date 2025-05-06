package com.eurodyn.service.anomaly_detection;

import com.eurodyn.dto.osint.request.AnomalyDetectionDto;
import com.eurodyn.dto.osint.response.InvestigationResponseDto;
import com.eurodyn.resttemplates.anomaly_detection.AnomalyDetectionRestTemplate;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AnomalyDetectionService {

    @Autowired
    AnomalyDetectionRestTemplate anomalyDetectionRestTemplate;

    public String anomalies(Map<String, Object> request) {
        return anomalyDetectionRestTemplate.anomalies(request);
    }
}
