package com.eurodyn.resttemplates.osint;

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
public class OsintRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${osint.uri}")
    private String osintUrl;

    public OsintRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public InvestigationResponseDto investigate(InvestigateDto investigateDto) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<InvestigateDto> httpEntity = new HttpEntity<>(investigateDto, httpHeaders);

        ResponseEntity<InvestigationResponseDto> response =
                restTemplate.exchange(
                        URI.create(osintUrl + "/investigate"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<InvestigationResponseDto>() {
                        }
                );

        return response.getBody();
    }

    public InvestigationResponseDto requestInvestigationResults(InvestigationResponseDto investigateResponseDto) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<InvestigationResponseDto> httpEntity = new HttpEntity<>(investigateResponseDto, httpHeaders);

        ResponseEntity<InvestigationResponseDto> response =
                restTemplate.exchange(
                        URI.create(osintUrl + "/request"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<InvestigationResponseDto>() {
                        }
                );

        return response.getBody();
    }

}
