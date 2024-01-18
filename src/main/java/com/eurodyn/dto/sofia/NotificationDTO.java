package com.eurodyn.dto.sofia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Builder
public class NotificationDTO {

    private String title;
    private String message;
    private String icon;
    private String receiverId;
    private String command;

}
