package com.eurodyn.dto.sofia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author vuktopalovic
 * @created 23/08/2023 - 11:09
 * @project sofia-plugin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Builder
public class LoginResponseDto {
    private String accessToken;
}
