package com.eurodyn.service.similarity_search;

import com.eurodyn.dto.osint.request.AnomalyDetectionDto;
import com.eurodyn.dto.osint.request.SimilaritySearchDto;
import com.eurodyn.resttemplates.anomaly_detection.AnomalyDetectionRestTemplate;
import com.eurodyn.resttemplates.similarity_search.SimilaritySearchRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SimilaritySearchService {

    @Autowired
    SimilaritySearchRestTemplate similaritySearchRestTemplate;

    @Autowired
    EntityManager entityManager;

    public String combinedOperation(SimilaritySearchDto similaritySearchDto) {
        return similaritySearchRestTemplate.combinedOperation(similaritySearchDto);
    }


    public List<Map<String, Object>> getFlights(Instant startDate, Instant endDate) {
        Query query = this.entityManager.createNativeQuery(
                " SELECT " +
                        " passenger.id AS passenger_id, " +
                        " pnr.id AS pnr_id, " +
                        " iata_pnrgov_notif_rq.id AS iata_pnrgov_notif_rq_id, " +
                        " iata_pnrgov_notif_rq.flight_leg_arrival_date_time AS ar_dat, " +
                        " iata_pnrgov_notif_rq.flight_leg_departure_date_time AS dep_dat, " +
                        " passenger.given_name, " +
                        " passenger.surname, " +
                        " iata_pnrgov_notif_rq.flight_leg_departure_airp_location_code, " +
                        " iata_pnrgov_notif_rq.flight_leg_arrival_airp_location_code, " +
                        " iata_pnrgov_notif_rq.flight_leg_flight_number, " +
                        " iata_pnrgov_notif_rq.originator_airline_code, " +
                        " iata_pnrgov_notif_rq.flight_leg_flight_number AS flight_number, " +
                        " iata_pnrgov_notif_rq.flight_leg_departure_date_time AS departure_date_time, " +
                        " iata_pnrgov_notif_rq.flight_leg_arrival_date_time AS arrival_date_time, " +
                        " pnr.booking_refid, " +
                        " doc_ssr.docs_first_givenname, " +
                        " doc_ssr.docs_surname, " +
                        " CONCAT( IFNULL(doc_ssr.docs_first_givenname,'') , ' ' , IFNULL(doc_ssr.docs_surname,'')) AS full_name, " +
                        " doc_ssr.doco_travel_doc_nbr, " +
                        " doc_ssr.doco_placeof_issue, " +
                        " doc_ssr.docs_dateof_birth, " +
                        " doc_ssr.docs_pax_nationality, " +
                        " doc_ssr.docs_gender, " +
                        " doc_ssr.doca_city_name, " +
                        " doc_ssr.doca_address " +
                        " FROM iata_pnrgov_notif_rq iata_pnrgov_notif_rq " +
                        " INNER JOIN pnr pnr ON iata_pnrgov_notif_rq.id = pnr.iata_pnrgov_notif_rq_id " +
                        " INNER JOIN passenger passenger ON pnr.id = passenger.pnr_id " +
                        " INNER JOIN doc_ssr doc_ssr ON doc_ssr.passenger_id = passenger.id " +
                        " WHERE iata_pnrgov_notif_rq.flight_leg_arrival_date_time BETWEEN :startDate AND :endDate " +
                        " ORDER BY iata_pnrgov_notif_rq.flight_leg_arrival_date_time DESC");

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        // Column names (make sure the order matches the SELECT statement)
        String[] columnNames = {
                "passenger_id", "pnr_id", "iata_pnrgov_notif_rq_id", "ar_dat", "dep_dat",
                "given_name", "surname", "flight_leg_departure_airp_location_code",
                "flight_leg_arrival_airp_location_code", "flight_leg_flight_number",
                "originator_airline_code", "flight_number", "departure_date_time",
                "arrival_date_time", "booking_refid", "docs_first_givenname",
                "docs_surname", "full_name", "doco_travel_doc_nbr",
                "doco_placeof_issue", "docs_dateof_birth", "docs_pax_nationality",
                "docs_gender", "doca_city_name", "doca_address"
        };

        List<Map<String, Object>> mappedResults = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                rowMap.put(columnNames[i], row[i]);
            }
            mappedResults.add(rowMap);
        }

        return mappedResults;
    }

}
