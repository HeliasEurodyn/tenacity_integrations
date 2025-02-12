package com.eurodyn.resttemplates.anomaly_detection;

import com.eurodyn.dto.osint.request.AnomalyDetectionDto;
import com.eurodyn.dto.osint.request.InvestigateDto;
import com.eurodyn.dto.osint.response.InvestigationResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class AnomalyDetectionRestTemplate {


    private final RestTemplate restTemplate;

    @Value("${anomaly.uri}")
    private String anomalyUrl;

    public AnomalyDetectionRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String anomalies(AnomalyDetectionDto anomalyDetectionDto) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<AnomalyDetectionDto> httpEntity = new HttpEntity<>(anomalyDetectionDto, httpHeaders);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        URI.create(anomalyUrl + "/getAnomalies"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<String>() {
                        }
                );

        return response.getBody();
    }

}
