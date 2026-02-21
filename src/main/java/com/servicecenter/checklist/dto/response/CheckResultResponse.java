package com.servicecenter.checklist.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Result of a single check item execution")
public class CheckResultResponse {

    @Schema(description = "ID of the check result", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Check item that was performed")
    private CheckItemResponse checkItem;

    @Schema(description = "Checklist type", example = "PRE_REPAIR")
    private String checklistType;

    @Schema(description = "Current status of the check item", example = "PENDING")
    private String status;

    @Schema(description = "Date and time when check was performed", example = "2026-02-14T15:00:00")
    private LocalDateTime checkedAt;

}
