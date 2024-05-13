package com.eurodyn.config;

import com.eurodyn.dto.osint.request.InvestigateDto;
import com.eurodyn.dto.sofia.LoginResponseDto;
import com.eurodyn.resttemplates.sofia.SofiaRestTemplate;
import com.eurodyn.service.osint.OsintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledOsintService {

    @Autowired
    SofiaRestTemplate sofiaRestTemplate;
    @Autowired
    private OsintService osintService;

    //    @Scheduled(cron = "0/30 * * * * *")
    public void myScheduledTask() {
        String token = "";

        // Your logic to be executed every 30 seconds
        List<InvestigateDto> investigateDtos = osintService.getPendingRequests();


        if (investigateDtos.size() > 0) {
            LoginResponseDto loginResponseDto = sofiaRestTemplate.login();
            token = loginResponseDto.getAccessToken();
        }

        for (InvestigateDto investigateDto : investigateDtos) {
            osintService.requestInvestigationResults(investigateDto, token);
        }

    }
}
