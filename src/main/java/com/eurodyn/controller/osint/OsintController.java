package com.eurodyn.controller.osint;

import com.eurodyn.dto.osint.request.InvestigateDto;
import com.eurodyn.dto.osint.response.InvestigationResponseDto;
import com.eurodyn.service.osint.OsintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/osint")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
public class OsintController {
    private final OsintService osintService;
    
    public OsintController(OsintService osintService) {
        this.osintService = osintService;
    }

    @PostMapping("/investigate")
    public InvestigationResponseDto investigate(@RequestBody InvestigateDto investigateDto,
                                                @RequestHeader("Authorization") String bearerToken) {
        return osintService.investigate(investigateDto, bearerToken);
    }

}
