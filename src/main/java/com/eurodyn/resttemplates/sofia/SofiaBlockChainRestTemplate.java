package com.eurodyn.resttemplates.sofia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
public class SofiaBlockChainRestTemplate {
    private final RestTemplate restTemplate;

    @Value("${sofia.uri}")
    private String sofiaUri;

    public SofiaBlockChainRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String post(Map<String, Map<String, Object>> parameters, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));
        HttpEntity<Map<String, Map<String, Object>>> httpEntity =
                new HttpEntity<Map<String, Map<String, Object>>>(parameters, httpHeaders);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/dataset/pnr-outgoing-request"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<String>() {
                        }
                );

        return response.getBody();
    }

    public Map<String, Object> postRules(Map<String, Object> parameters, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));
        HttpEntity<Map<String, Object>> httpEntity =
                new HttpEntity<Map<String, Object>>(parameters, httpHeaders);

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/rule"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );

        return response.getBody();
    }

}

