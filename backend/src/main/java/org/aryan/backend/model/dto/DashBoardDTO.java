package org.aryan.backend.model.dto;

import java.util.List;

public record DashBoardDTO(
        int totalMonitorCount,
        int activeMonitorCount,
        int operationalMonitors,
        int degradedMonitors,
        int stormAlert,
        double overallUptime,
        String degradedMessage,
        String stormMessage,

        List<MonitorResponseDTO> monitors
) {
}
