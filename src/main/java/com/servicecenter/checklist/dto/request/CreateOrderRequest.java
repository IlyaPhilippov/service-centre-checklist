package com.servicecenter.checklist.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Request for creating order")
public class CreateOrderRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Client's name", example = "Ivan Ivanov", requiredMode = Schema.RequiredMode.REQUIRED)
    private String clientName;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Device Type", example = "SMARTPHONE")
    private String deviceType;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Device Brand", example = "Apple")
    private String deviceBrand;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Device Model", example = "iPhone 11")
    private String deviceModel;

}
