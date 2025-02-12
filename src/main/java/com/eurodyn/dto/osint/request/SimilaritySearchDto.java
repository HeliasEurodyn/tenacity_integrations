package com.eurodyn.dto.osint.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SimilaritySearchDto {

    String arrival_date_from;
    String arrival_date_to;
    String flight_nbr;
    String firstname;
    String surname;

    String dob;
    String iata_o;
    String iata_d;
    String city_name;
    String address;

    String sex;
    String nationality;
    Double nameThreshold;
    Double ageThreshold;
    Double locationThreshold;
}
