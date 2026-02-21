package com.servicecenter.checklist.controller;

import com.servicecenter.checklist.dto.request.CreateOrderRequest;
import com.servicecenter.checklist.dto.request.UpdateOrderRequest;
import com.servicecenter.checklist.dto.response.OrderResponse;
import com.servicecenter.checklist.entity.RepairOrder;
import com.servicecenter.checklist.mapper.OrderMapper;
import com.servicecenter.checklist.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Repair orders", description = "Repair orders controller")
public class RepairOrderController {

    private final RepairOrderService repairOrderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "Get all repair orders", description = "Returns list of all repair orders")
    @ApiResponse(responseCode = "200", description = "Success!")
    @ApiResponse(responseCode = "500", description = "Iternal error!")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("REST request to get all repair orders");

        List<RepairOrder> repairOrders = repairOrderService.getAllRepairOrders();
        log.debug("Returning {} orders", repairOrders.size());

        return ResponseEntity.ok(orderMapper.toResponseList(repairOrders));
    }

    @Operation(summary = "Get repair order by id", description = "Return repair order by id")
    @ApiResponse(responseCode = "200", description = "Repair order found!")
    @ApiResponse(responseCode = "404", description = "Repair order not found!")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        log.info("REST request to get order: {}", id);

        RepairOrder repairOrder = repairOrderService.getRepairOrderById(id);

        return ResponseEntity.ok(orderMapper.toResponse(repairOrder));
    }

    @Operation(summary = "Create repair order", description = "Creates new repair order")
    @ApiResponse(responseCode = "201", description = "Repair order created!")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "500", description = "Iternal server error")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("REST request to create order for client: {}", request.getClientName());

        RepairOrder repairOrder = orderMapper.toEntity(request);

        RepairOrder savedOrder = repairOrderService.createRepairOrder(repairOrder);
        log.info("Order created with ID: {}", savedOrder.getId());

        return new ResponseEntity<>(orderMapper.toResponse(savedOrder), HttpStatus.CREATED);
    }

    @Operation(summary = "Update repair order", description = "Updates existing repair order by id")
    @ApiResponse(responseCode = "200", description = "Repair order updated")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "404", description = "Repair order not found")
    @ApiResponse(responseCode = "500", description = "Iternal server error")
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderRequest request) {
        log.info("REST request to update order: {}", id);

        if (!id.equals(request.getId())) {
            log.error("REST request to update order: id mismatch");
            throw new IllegalArgumentException("ID in path and body must match");
        }

        RepairOrder updatedOrder = repairOrderService.updateRepairOrder(request);
        log.info("Order {} updated successfully", id);

        return ResponseEntity.ok(orderMapper.toResponse(updatedOrder));
    }

    @Operation(summary = "Delete repair order", description = "Deletes repair order by id")
    @ApiResponse(responseCode = "204", description = "Repair order deleted successfully")
    @ApiResponse(responseCode = "404", description = "Repair order not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        log.info("REST request to delete order: {}", id);

        repairOrderService.deleteRepairOrder(id);
        log.info("Order {} deleted successfully", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create post-repair checklist", description = "Creates post-repair checklist for existing order")
    @ApiResponse(responseCode = "201", description = "Checklist created")
    @ApiResponse(responseCode = "404", description = "Repair order not found")
    @PostMapping("/{id}/post-repair")
    public ResponseEntity<OrderResponse> createPostRepairChecklist(@PathVariable UUID id) {
        log.info("REST request to create POST_REPAIR checklist for order: {}", id);

        RepairOrder order = repairOrderService.createPostRepairChecklist(id);
        log.info("POST_REPAIR checklist created for order: {}", id);

        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}