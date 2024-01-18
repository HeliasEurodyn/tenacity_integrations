package com.eurodyn.resttemplates.sofia;

import com.eurodyn.dto.sofia.LoginDto;
import com.eurodyn.dto.sofia.LoginResponseDto;
import com.eurodyn.dto.sofia.NotificationDTO;
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
public class SofiaRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${sofia.uri}")
    private String sofiaUri;

    public SofiaRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object tokenValidationCheck(String token) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", token);

        HttpEntity<LoginDto> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Object> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/user/hello"),
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<Object>() {
                        }
                );

        return response.getBody();
    }

    public LoginResponseDto login() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        LoginDto loginDto = LoginDto.builder()
                .username("testUser")
                .password("123")
                .build();

        HttpEntity<LoginDto> httpEntity = new HttpEntity<>(loginDto, httpHeaders);

        ResponseEntity<LoginResponseDto> response =
                restTemplate.exchange(
                        URI.create(sofiaUri + "/user/auth"),
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<LoginResponseDto>() {
                        }
                );

        return response.getBody();
    }

    public void sendNotification(NotificationDTO notification, String token) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", token);

        HttpEntity<NotificationDTO> httpEntity = new HttpEntity<>(notification, httpHeaders);

        restTemplate.exchange(
                URI.create(sofiaUri + "/notification/plain"),
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Void>() {
                });
    }

}
