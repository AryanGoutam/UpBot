package org.aryan.backend.model.dto;

public record WebsiteRequest(
         Long userId,
         String name,
         String url,
         String status,
         Integer check_interval
) {
}
