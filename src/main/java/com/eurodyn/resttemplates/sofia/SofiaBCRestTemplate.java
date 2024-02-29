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
import java.util.HashMap;
import java.util.Map;

@Service
public class SofiaBCRestTemplate {
    private final RestTemplate restTemplate;

    @Value("${sofia.uri}")
    private String sofiaUri;

    public SofiaBCRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String postOutgoingRequest(Map<String, Map<String, Object>> parameters, Map<String, String> headers) {

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

    public String postIncomingRequest(Map<String, Map<String, Object>> parameters, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));
        HttpEntity<Map<String, Map<String, Object>>> httpEntity =
                new HttpEntity<Map<String, Map<String, Object>>>(parameters, httpHeaders);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/dataset/pnr-incoming-request"),
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

    public Map<String, Object> getPassenger(String passengerId, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));
        HttpEntity httpEntity =
                new HttpEntity(httpHeaders);

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/dataset/passenger/" + passengerId),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );

        return response.getBody();
    }

    public Map<String, String> setPassenger(Map<String, Object> passengerDataset, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));
        HttpEntity<Map<String, String>> httpEntity =
                new HttpEntity(passengerDataset, httpHeaders);

        ResponseEntity<Map<String, String>> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/dataset/v2/passenger/"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                );

        return response.getBody();
    }

    public void setRequestIdToPnr(String pnrId, String blockChainRequestId, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("blockchain_request_id", blockChainRequestId);
        requestBody.put("pnr_id", pnrId);

        HttpEntity<Map<String, String>> httpEntity =
                new HttpEntity(requestBody, httpHeaders);

        restTemplate.exchange(
                URI.create(sofiaUri + "custom-query/data-objects/blockchain-update-request-id"),
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

    public void setRejectionToPnr(String requestId, String message, Map<String, String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", headers.get("authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("request_id", requestId);
        requestBody.put("message", message);

        HttpEntity<Map<String, String>> httpEntity =
                new HttpEntity(requestBody, httpHeaders);

        restTemplate.exchange(
                URI.create(sofiaUri + "custom-query/data-objects/blockchain-update-rejection"),
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Object>() {
                }
        );
    }

}

