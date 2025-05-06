package com.eurodyn.controller.anomaly_detection;

import com.eurodyn.dto.osint.request.AnomalyDetectionDto;
import com.eurodyn.service.anomaly_detection.AnomalyDetectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/anomaly-detection")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
public class AnomalyDetectionController {

    private final AnomalyDetectionService anomalyDetectionService;

    public AnomalyDetectionController(AnomalyDetectionService anomalyDetectionService) {
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @PostMapping(value ="/anomalies", produces = MediaType.APPLICATION_JSON_VALUE)
    public String anomalies(@RequestBody Map<String, Object> request) {
        return anomalyDetectionService.anomalies(request);
    }

}
