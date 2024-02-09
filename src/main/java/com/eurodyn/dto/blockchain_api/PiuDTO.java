package com.eurodyn.dto.blockchain_api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PiuDTO {
    private String id;
    private String name;
    private String adminEmail;


}
