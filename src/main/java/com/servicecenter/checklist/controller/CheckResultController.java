package com.servicecenter.checklist.controller;

import com.servicecenter.checklist.dto.response.CheckResultResponse;
import com.servicecenter.checklist.entity.CheckResult;
import com.servicecenter.checklist.mapper.OrderMapper;
import com.servicecenter.checklist.service.CheckResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/check-results")
@RequiredArgsConstructor
@Tag(name = "Check Results", description = "Operations with check results")
public class CheckResultController {

    private final CheckResultService checkResultService;
    private final OrderMapper orderMapper;

    @PostMapping("{id}/status")
    public ResponseEntity<CheckResultResponse> updateStatus(@PathVariable UUID id, @RequestParam String status){
        log.info("REST request to update check result {} to status {}", id, status);

        if (!List.of("PASSED", "FAILED", "PENDING", "NOT_APPLICABLE").contains(status)) {
            log.error("Invalid status: {}", status);
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        CheckResult checkResult = checkResultService.updateStatus(id, status);

        return ResponseEntity.ok(orderMapper.toCheckResultResponse(checkResult));
    }
}
