package com.eurodyn.controller.similarity_search;

import com.eurodyn.Application;
import com.eurodyn.dto.osint.request.AnomalyDetectionDto;
import com.eurodyn.dto.osint.request.SimilaritySearchDto;
import com.eurodyn.service.anomaly_detection.AnomalyDetectionService;
import com.eurodyn.service.similarity_search.SimilaritySearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/similarity-search")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
public class SimilaritySearchController {


    private final SimilaritySearchService similaritySearchService;

    public SimilaritySearchController(SimilaritySearchService similaritySearchService) {
        this.similaritySearchService = similaritySearchService;
    }

    @PostMapping(value = "/combined_operation", produces = MediaType.APPLICATION_JSON_VALUE )
    public String combinedOperation(@RequestBody SimilaritySearchDto similaritySearchDto) {
        return similaritySearchService.combinedOperation(similaritySearchDto);
    }

    @GetMapping(value = "/flights", produces = MediaType.APPLICATION_JSON_VALUE )
    public List<Map<String, Object>> getFlights( @RequestParam("startArDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
                                                 @RequestParam("endArDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        return similaritySearchService.getFlights(startDate, endDate);
    }
}
