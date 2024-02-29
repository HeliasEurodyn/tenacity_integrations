package com.eurodyn.repository.osint;

import com.eurodyn.dto.osint.request.AddressDto;
import com.eurodyn.dto.osint.request.InvestigateDto;
import com.eurodyn.dto.osint.request.PersonDto;
import com.eurodyn.dto.osint.response.InvestigationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OsintRepository {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Transactional
    public void setRiskAssessmentResults(InvestigationResponseDto investigateResultsDto, String requestId) {

        String spQueryString =
                " UPDATE risk_assesment_result " +
                        " SET " +
                        " osint_status = 'finished', " +
                        " osint_confidence = :osint_confidence, " +
                        " osint_value = :osint_value " +
                        " WHERE " +
                        " osint_requestid = :request_id";

        Query spQuery = entityManager.createNativeQuery(spQueryString);

        spQuery.setParameter("request_id", requestId);
        spQuery.setParameter("osint_confidence", investigateResultsDto.getPerson().getRiskAssessment().get(0).getConfidence());
        spQuery.setParameter("osint_value", investigateResultsDto.getPerson().getRiskAssessment().get(0).getValue());

        transactionTemplate.execute(transactionStatus -> {
            spQuery.executeUpdate();
            transactionStatus.flush();
            return null;
        });
    }

    public void setRiskAssessmentResultPending(InvestigateDto investigateDto, String requestId, String osintInvestigator) {

        String spQueryString =
                " UPDATE risk_assesment_result " +
                        " SET " +
                        " osint_status = 'pending', " +
                        " osint_requestid = :request_id , " +
                        " osint_investigator = :osint_investigator , " +
                        " osin_started_on = NOW() " +
                        " WHERE " +
                        " risk_assesment_result.id = :risk_assesment_result_id";

        Query spQuery = entityManager.createNativeQuery(spQueryString);

        spQuery.setParameter("request_id", requestId);
        spQuery.setParameter("risk_assesment_result_id", investigateDto.getRisk_assesment_result_id());
        spQuery.setParameter("osint_investigator", osintInvestigator);


        transactionTemplate.execute(transactionStatus -> {
            spQuery.executeUpdate();
            transactionStatus.flush();
            return null;
        });
    }

    public InvestigateDto getInvestigationData(String riskΑssesmentResultΙd) {

        InvestigateDto investigateDto = null;

        Query query = this.entityManager.createNativeQuery(
                "SELECT rar.id, " +
                        "p.given_name, " +
                        "p.surname, " +
                        "ds.docs_dateof_birth, " +
                        "(CASE WHEN ds.docs_gender = 'F' THEN 'Female' WHEN ds.docs_gender = 'M' THEN 'Male' ELSE ds.docs_gender END) AS docs_gender, " +
                        "ds.doca_country, " +
                        "ds.doca_city_name, " +
                        "ra.name, " +
                        "rar.osint_investigator " +
                        "FROM risk_assesment_result rar " +
                        "INNER JOIN passenger p ON p.id = rar.passenger_id " +
                        "INNER JOIN doc_ssr ds ON ds.passenger_id = p.id " +
                        "INNER JOIN risk_assesment ra ON ra.id = rar.risk_assesment_id " +
                        "WHERE rar.id = :risk_assesment_result_id");

        query.setParameter("risk_assesment_result_id", riskΑssesmentResultΙd);

        List<Object[]> rows = query.getResultList();

        for (Object[] row : rows) {
            investigateDto = new InvestigateDto();
            investigateDto.setRisk_assesment_result_id(riskΑssesmentResultΙd);
            investigateDto.setTimestamp(Instant.now());
            investigateDto.setCrimeTopic(Collections.singletonList("human-trafficking"));
            investigateDto.setRiskAssessmentName(row[7] == null ? "" : row[7].toString());
            investigateDto.setRiskAssessmentOwnerId(row[8] == null ? "" : row[8].toString());

            PersonDto person = new PersonDto();
            person.setFirstName(row[1].toString());
            person.setLastName(row[2].toString());
            person.setBirthDate(row[3].toString());
            person.setGender(row[4].toString());

            AddressDto address = new AddressDto();
            address.setCountry(row[5].toString());
            address.setCity(row[6].toString());

            person.setAddress(address);
            investigateDto.setPerson(person);
        }

        return investigateDto;
    }

    public List<InvestigateDto> getPendingRequests() {
        List<InvestigateDto> investigateDtos = new ArrayList<>();
        Query query = this.entityManager.createNativeQuery(
                "SELECT osint_requestid, id " +
                        "FROM risk_assesment_result WHERE osint_status = 'pending' ");

        List<Object[]> rows = query.getResultList();

        for (Object[] row : rows) {
            InvestigateDto investigateDto = new InvestigateDto();
            investigateDto.setRequestId(row[0].toString());
            investigateDto.setRisk_assesment_result_id(row[1].toString());
            investigateDtos.add(investigateDto);
        }

        return investigateDtos;
    }
}
