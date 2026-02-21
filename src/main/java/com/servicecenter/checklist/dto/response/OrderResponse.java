package com.servicecenter.checklist.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

    @Schema(description = "ID of response checklist", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Client Name", example = "Ivan Ivanov")
    private String clientName;

    @Schema(description = "Device Type", example = "SMARTPHONE")
    private String deviceType;

    @Schema(description = "Device Brand", example = "Apple")
    private String deviceBrand;

    @Schema(description = "Device Model", example = "iPhone 11")
    private String deviceModel;

    @Schema(description = "Date and time when response was created", example = "2026-02-14T15:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Order status", example = "IN_PROGRESS")
    private String status;

    @Schema(description = "List of check items for repair order")
    private List<CheckResultResponse> checkResults;

}