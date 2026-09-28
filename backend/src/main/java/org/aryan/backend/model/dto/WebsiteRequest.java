package org.aryan.backend.model.dto;

public record WebsiteRequest(
         String name,
         String url,
         String status,
         Integer check_interval
) {
}
