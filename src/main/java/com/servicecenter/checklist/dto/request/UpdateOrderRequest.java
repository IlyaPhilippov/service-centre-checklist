package com.servicecenter.checklist.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Request for updating an order")
public class UpdateOrderRequest {

    @Schema(description = "Order ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Client's name", example = "Ivan Ivanov")
    @Size(max = 255, message = "Client name must be less than 255 characters")
    private String clientName;

    @Schema(description = "Device Type", example = "SMARTPHONE",
            allowableValues = {"SMARTPHONE", "LAPTOP", "TABLET"})
    @Size(max = 50, message = "Device type must be less than 50 characters")
    private String deviceType;

    @Schema(description = "Device Brand", example = "Apple")
    @Size(max = 50, message = "Device brand must be less than 50 characters")
    private String deviceBrand;

    @Schema(description = "Device Model", example = "iPhone 11")
    @Size(max = 100, message = "Device model must be less than 100 characters")
    private String deviceModel;
}