package com.eurodyn.service.osint;

import com.eurodyn.dto.osint.request.InvestigateDto;
import com.eurodyn.dto.osint.response.InvestigationResponseDto;
import com.eurodyn.dto.sofia.NotificationDTO;
import com.eurodyn.dto.sofia.UserDTO;
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

    public InvestigationResponseDto investigate(InvestigateDto investigateDto, String bearerToken) {

        /*
         * Check If The token Is Valid
         * */
        UserDTO userDTO = this.sofiaRestTemplate.getCurrentUser(bearerToken);

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
        this.osintRepository.setRiskAssessmentResultPending(investigateDto, investigateResponseDto.getRequestId(), userDTO.getId());

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

        if ((investigateResultsDto.getDone() == null ? "" : investigateResultsDto.getDone())
                .equals("false")) {
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
                .command("{\"COMMAND-TYPE\":\"FORM\",\"LOCATE\":{\"ID\":\"ebd3e7d1-99fb-4a9f-8b48-0e55fc631a9e\",\"SELECTION-ID\":\"" +
                        investigateDto.getRisk_assesment_result_id()
                        + "\"}}")
                .build();

        this.sofiaRestTemplate.sendNotification(notificationDTO, token);
    }

    public List<InvestigateDto> getPendingRequests() {
        return this.osintRepository.getPendingRequests();
    }

}
