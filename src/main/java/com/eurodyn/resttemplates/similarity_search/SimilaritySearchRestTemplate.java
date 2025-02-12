package com.eurodyn.resttemplates.similarity_search;

import com.eurodyn.dto.osint.request.AnomalyDetectionDto;
import com.eurodyn.dto.osint.request.SimilaritySearchDto;
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
public class SimilaritySearchRestTemplate {


    private final RestTemplate restTemplate;

    @Value("${similarity_search.uri}")
    private String similaritySearchUri;

    public SimilaritySearchRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String combinedOperation(SimilaritySearchDto similaritySearchDto) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<SimilaritySearchDto> httpEntity = new HttpEntity<>(similaritySearchDto, httpHeaders);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        URI.create(similaritySearchUri + "/combined_operation"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<String>() {
                        }
                );

        return response.getBody();
    }

}
