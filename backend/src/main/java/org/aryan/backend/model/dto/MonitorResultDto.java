package org.aryan.backend.model.dto;

import java.time.LocalDateTime;

public record MonitorResultDto(
         LocalDateTime checkedAt,
         boolean up,
         Integer statusCode,
         Long responseTime,
         String errorMessage
        ) {
}
