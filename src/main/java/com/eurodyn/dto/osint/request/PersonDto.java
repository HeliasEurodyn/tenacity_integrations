package com.eurodyn.dto.osint.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PersonDto {
    String firstName;
    String lastName;
    String gender;
    String birthDate;
    AddressDto address;
}
