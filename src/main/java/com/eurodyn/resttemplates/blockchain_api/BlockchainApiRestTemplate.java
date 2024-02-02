package com.eurodyn.resttemplates.blockchain_api;

import com.eurodyn.dto.blockchain_api.RequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class BlockchainApiRestTemplate {
    private final RestTemplate restTemplate;

    @Value("${blockchain.uri}")
    private String blockchainUri;

    public BlockchainApiRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String post(RequestDTO requestDTO, String uuid) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<RequestDTO> httpEntity =
                new HttpEntity(requestDTO, httpHeaders);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        URI.create(blockchainUri + "/PNR/request?targetPIUId=" + uuid),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<String>() {
                        }
                );

        return response.getBody();
    }

    public List<Map<String, Object>> getPnrs() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<RequestDTO> httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        URI.create(blockchainUri + "/PNR"),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        }
                );

        return response.getBody();
    }

    public List<Map<String, String>> getPius() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        //  httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<List<Map<String, String>>> response =
                restTemplate.exchange(
                        URI.create(blockchainUri + "/PIU"),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<List<Map<String, String>>>() {
                        }
                );

        return response.getBody();

    }
}
