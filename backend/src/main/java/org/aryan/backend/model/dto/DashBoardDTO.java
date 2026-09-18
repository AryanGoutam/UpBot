package org.aryan.backend.model.dto;

import java.util.List;

public record DashBoardDTO(
        int totalMonitorCount,
        int activeMonitorCount,
        int operationalMonitors,
        int degradedMonitors,
        int stormAlert,
        int overallUptime,
        String degradedMessage,
        String stormMessage,

        List<MonitorResponseDTO> monitors
) {
}
