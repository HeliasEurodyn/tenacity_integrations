package com.eurodyn.service.osint;

import com.eurodyn.dto.osint.request.InvestigateDto;
import com.eurodyn.dto.osint.response.InvestigationResponseDto;
import com.eurodyn.dto.sofia.NotificationDTO;
import com.eurodyn.repository.osint.OsintRepository;
import com.eurodyn.resttemplates.osint.OsintRestTemplate;
import com.eurodyn.resttemplates.sofia.SofiaRestTemplate;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@ToString
public class OsintService {

    @Autowired
    OsintRestTemplate osintRestTemplate;

    @Autowired
    SofiaRestTemplate sofiaRestTemplate;

    @Autowired
    OsintRepository osintRepository;

//    public InvestigationResponseDto investigate(InvestigateDto investigateDto, String bearerToken) {
//
//        /*
//         * Check If The token Is Valid
//         * */
//        this.sofiaRestTemplate.tokenValidationCheck(bearerToken);
//
//        /*
//         * Request For Investigation By Osint
//         * */
//        InvestigationResponseDto investigateResponseDto = osintRestTemplate.investigate(investigateDto);
//
//        /*
//         * Update Status of risk_assesment_result  to pending
//         * */
//        this.osintRepository.setRiskAssessmentResultPending(investigateDto, investigateResponseDto.getRequestId());
//
//        /*
//         *
//         * */
//        return investigateResponseDto;
//    }

    public InvestigationResponseDto investigate(InvestigateDto investigateDto, String bearerToken) {

        /*
         * Check If The token Is Valid
         * */
        this.sofiaRestTemplate.tokenValidationCheck(bearerToken);

        /*
         * Get All The Data
         * */
        investigateDto = this.osintRepository.getInvestigationData(investigateDto.getRisk_assesment_result_id());

        /*
         * Request For Investigation By Osint
         * */
        InvestigationResponseDto investigateResponseDto = osintRestTemplate.investigate(investigateDto);

        /*
         * Update Status of risk_assesment_result to pending
         * */
        this.osintRepository.setRiskAssessmentResultPending(investigateDto, investigateResponseDto.getRequestId());

        /*
         *
         * */
        return investigateResponseDto;
    }

    public void requestInvestigationResults(InvestigateDto investigateDto, String token) {

        /*
         * Request For Investigation By Osint
         * */
        InvestigationResponseDto investigationResponseDto = new InvestigationResponseDto();
        investigationResponseDto.setRequestId(investigateDto.getRequestId());
        InvestigationResponseDto investigateResultsDto = osintRestTemplate.requestInvestigationResults(investigationResponseDto);
        log.info(investigateResultsDto.toString());

        if (investigateResultsDto.getDone() == null) {
            return;
        }

        if (investigateResultsDto.getDone().equals("false")) {
            return;
        }

        /*
         * Update Status of risk_assesment_result to finished
         * */
        this.osintRepository.setRiskAssessmentResults(investigateResultsDto, investigateDto.getRequestId());

        investigateDto = this.osintRepository.getInvestigationData(investigateDto.getRisk_assesment_result_id());

        NotificationDTO notificationDTO = NotificationDTO.builder().
                title("Osint Investigation Result")
                .message("Passenger <i class=\"fa fa-user\"></i> " +
                        investigateDto.getPerson().getFirstName() + " " +
                        investigateDto.getPerson().getLastName() + " " +
                        " of <i class=\"fa fa-play\"></i> " +
                        investigateDto.getRiskAssessmentName() +
                        " with value <b>" +
                        investigateResultsDto.getPerson().getRiskAssessment().get(0).getValue()
                        + "</b> and Confidence of <b>" +
                        investigateResultsDto.getPerson().getRiskAssessment().get(0).getConfidence()
                        + "</b>.")
                .icon("fa-square-poll-horizontal")
                .receiverId(investigateDto.getRiskAssessmentOwnerId())
                .command("{\"COMMAND-TYPE\":\"FORM\",\"LOCATE\":{\"ID\":\"2be5dd52-5d3a-4193-969d-e5638ba9a1b8\",\"SELECTION-ID\":\"" +
                        investigateDto.getRisk_assesment_result_id()
                        + "\"}}")
                .build();

        this.sofiaRestTemplate.sendNotification(notificationDTO, token);
    }

    public List<InvestigateDto> getPendingRequests() {
        return this.osintRepository.getPendingRequests();
    }

}
