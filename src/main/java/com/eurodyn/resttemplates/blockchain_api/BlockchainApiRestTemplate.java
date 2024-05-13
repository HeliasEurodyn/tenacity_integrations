package com.eurodyn.resttemplates.blockchain_api;

import com.eurodyn.dto.blockchain_api.AcknowledgeDTO;
import com.eurodyn.dto.blockchain_api.PiuDTO;
import com.eurodyn.dto.blockchain_api.RequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlockchainApiRestTemplate {
    private final RestTemplate restTemplate;

    @Value("${blockchain.requester.uri}")
    private String blockchainRequesterUri;
    @Value("${blockchain.requester.id}")
    private String blockchainRequesterId;
    @Value("${blockchain.responder.uri}")
    private String blockchainResponderUri;
    @Value("${blockchain.responder.id}")
    private String blockchainResponderId;


    public BlockchainApiRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String request(RequestDTO requestDTO, String uuid) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("requestData", requestDTO);

        HttpEntity httpEntity =
                new HttpEntity(requestBody, httpHeaders);

        ResponseEntity<Map<String, String>> response =
                restTemplate.exchange(
                        URI.create(blockchainRequesterUri + "/PNR/request?respondingPIU=" + uuid),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                );

        return response.getBody().get("requestId");
    }

    public void confirm(String respondingPIU, String requestId) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("requestId", requestId);

        HttpEntity httpEntity =
                new HttpEntity(requestBody, httpHeaders);

        // ResponseEntity<Map<String, String>> response =
        restTemplate.exchange(
                URI.create(blockchainRequesterUri + "/PNR/confirm?respondingPIU=" + respondingPIU),
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        //   return response.getBody().get("requestId");
    }

    public List<Map<String, Object>> getAckPnrs() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<RequestDTO> httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        URI.create(blockchainRequesterUri + "/PNR?state=Ack&confirmed=false&requestingPIU=" + blockchainRequesterId),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        }
                );

        return response.getBody();
    }

    public List<Map<String, Object>> getNackPnrs() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<RequestDTO> httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        URI.create(blockchainRequesterUri + "/PNR?state=Nack&confirmed=false&requestingPIU=" + blockchainRequesterId),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        }
                );

        return response.getBody();
    }

    public List<Map<String, Object>> getPendingPnrs() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<RequestDTO> httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        URI.create(blockchainResponderUri + "/PNR?state=Pending&confirmed=false&respondingPIU=" + blockchainResponderId),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        }
                );

        return response.getBody();
    }

    //    public List<Map<String, String>> getPius() {
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Content-Type", "application/json");
//        //  httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//        HttpEntity httpEntity = new HttpEntity(httpHeaders);
//
//        ResponseEntity<List<Map<String, String>>> response =
//                restTemplate.exchange(
//                        URI.create(blockchainUri + "/PIU"),
//                        HttpMethod.GET,
//                        httpEntity,
//                        new ParameterizedTypeReference<List<Map<String, String>>>() {
//                        }
//                );
//
//        return response.getBody();
//
//    }
    public List<PiuDTO> getPius() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<List<PiuDTO>> response =
                restTemplate.exchange(
                        URI.create(blockchainRequesterUri + "/PIU"),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<List<PiuDTO>>() {
                        }
                );
        return response.getBody();
    }

    public Map acknowledge(AcknowledgeDTO acknowledgeDTO, String uuid, String requestId) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("requestId", requestId);
        requestBody.put("responseData", acknowledgeDTO);

        HttpEntity httpEntity =
                new HttpEntity(requestBody, httpHeaders);

        ResponseEntity<Map<String, String>> response =
                restTemplate.exchange(
                        URI.create(blockchainResponderUri + "/PNR/response/ack?requestingPIU=" + uuid),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                );

        return response.getBody();
    }

    public String reject(String blockchainRejectionMessage, String uuid, String requestId) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("requestId", requestId);
        requestBody.put("responseData", Collections.singletonMap("message", blockchainRejectionMessage));

        HttpEntity httpEntity =
                new HttpEntity(requestBody, httpHeaders);

        ResponseEntity<Map<String, String>> response =
                restTemplate.exchange(
                        URI.create(blockchainResponderUri + "/PNR/response/nack?requestingPIU=" + uuid),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Map<String, String>>() {
                        }
                );

        return response.getBody().get("requestId");
    }


}
