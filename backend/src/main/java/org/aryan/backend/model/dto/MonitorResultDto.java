package org.aryan.backend.model.dto;

import java.time.LocalDateTime;

public record MonitorResultDto(
         LocalDateTime checked_at,
         boolean up,
         Integer status_code,
         Long response_time,
         String error_message
        ) {
}
