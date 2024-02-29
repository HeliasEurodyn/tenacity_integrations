package com.eurodyn.config;

import com.eurodyn.dto.sofia.LoginResponseDto;
import com.eurodyn.resttemplates.sofia.SofiaRestTemplate;
import com.eurodyn.service.blockchain_api.BlockchainApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScheduledBlockChainService {

    @Autowired
    SofiaRestTemplate sofiaRestTemplate;

    @Autowired
    BlockchainApiService blockchainApiService;

    @Scheduled(cron = "0/30 * * * * *")
    public void myScheduledTask() {

        LoginResponseDto loginResponseDto = sofiaRestTemplate.login();
        String token = loginResponseDto.getAccessToken();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer " + token);

        blockchainApiService.syncPnrs(headers);
        blockchainApiService.syncAcknowledge(headers);
        blockchainApiService.syncReject(headers);
    }


    @Scheduled(cron = "0 0/30 * * * *")
    public void syncPius() {
        blockchainApiService.syncPius();
    }

}
