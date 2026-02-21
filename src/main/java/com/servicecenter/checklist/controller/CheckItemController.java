package com.servicecenter.checklist.controller;

import com.servicecenter.checklist.dto.response.CheckItemResponse;
import com.servicecenter.checklist.mapper.OrderMapper;
import com.servicecenter.checklist.service.CheckItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/check-items")
@RequiredArgsConstructor
@Tag(name = "Check Items", description = "Operations with check item templates")
public class CheckItemController {

    private final CheckItemService checkItemService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Get checks by device type", description = "Returns all checks for device type")
    @ApiResponse(responseCode = "200", description = "Success!")
    @ApiResponse(responseCode = "400", description = "Invalid device type parameter")
    @ApiResponse(responseCode = "500", description = "Iternal error!")
    @GetMapping
    public List<CheckItemResponse> getChecks(@RequestParam String deviceType){
        log.info("REST request to get checks for device type: {}", deviceType);

        List<CheckItemResponse> checks = checkItemService.getActiveChecksForDevice(deviceType).stream()
                .map(orderMapper::toCheckItemResponse)
                .toList();
        log.debug("Returning {} checks for {}", checks.size(), deviceType);

        return checks;
    }
}
