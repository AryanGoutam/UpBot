package org.aryan.backend.model.dto;


import java.util.List;

public record MonitorResponseDTO(
        Long id,
        String name,
        String url,
        String status,
        Long latency,
        double uptime24h,
        int interval,
        int score,
        String scoreStatus,
        List<Double> history


) {
}
