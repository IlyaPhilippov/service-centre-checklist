package com.servicecenter.checklist.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckItemResponse {

    @Schema(description = "Check Item ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Check Name", example = "Phone turns on?")
    private String name;

    @Schema(description = "Category", example = "BASIC")
    private String category;

    @Schema(description = "Unique key for dependency tracking", example = "PHONE_ON")
    private String checkKey;

    @Schema(description = "Required checks that must PASS for this check to be applicable",
            example = "PHONE_ON,DISPLAY_WORKS")
    private String requiredChecks;

}
