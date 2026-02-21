package com.servicecenter.checklist.service;

import com.servicecenter.checklist.dto.request.UpdateOrderRequest;
import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.entity.CheckResult;
import com.servicecenter.checklist.entity.RepairOrder;
import com.servicecenter.checklist.exception.ResourceNotFoundException;
import com.servicecenter.checklist.mapper.OrderMapper;
import com.servicecenter.checklist.repository.RepairOrderRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private final EntityManager entityManager;
    private final RepairOrderRepository repairOrderRepository;
    private final CheckItemService checkItemService;
    private final CheckResultService checkResultService;
    private final OrderMapper orderMapper;

    @Transactional
    public RepairOrder createRepairOrder(RepairOrder repairOrder) {

        log.info("Creating new repair order for client: {}", repairOrder.getClientName());

        RepairOrder savedOrder = repairOrderRepository.save(repairOrder);
        log.debug("Order saved with id: {}", savedOrder.getId());

        List<CheckItem> checkItems = checkItemService.getActiveChecksForDevice(savedOrder.getDeviceType());
        log.debug("Found {} check items for device type: {}", checkItems.size(), savedOrder.getDeviceType());

        checkResultService.createPreRepairChecklist(savedOrder, checkItems);
        log.info("Pre-repair checklist created for order: {}", repairOrder.getId());

        repairOrderRepository.flush();
        entityManager.clear();

        RepairOrder result = getRepairOrderById(savedOrder.getId());
        log.info("Order {} successfully created with {} checks", repairOrder.getId(), result.getCheckResults().size());

        return result;
    }

    @Transactional
    public RepairOrder createPostRepairChecklist(UUID orderId) {
        log.info("Creating POST_REPAIR checklist for order: {}", orderId);

        RepairOrder repairOrder = getRepairOrderById(orderId);

        List<CheckItem> checkItems = checkItemService.getActiveChecksForDevice(
                repairOrder.getDeviceType()
        );

        checkResultService.createPostRepairChecklist(repairOrder, checkItems);
        log.debug("POST_REPAIR checklist created with {} items", checkItems.size());

        repairOrderRepository.flush();
        entityManager.clear();

        RepairOrder result = getRepairOrderById(orderId);
        log.info("POST_REPAIR checklist completed for order: {}", orderId);

        return result;
    }

    @Cacheable(value = "orders", key = "#id")
    @Transactional(readOnly = true)
    public RepairOrder getRepairOrderById(UUID id) {
        log.debug("Fetching order by id: {}", id);

        return repairOrderRepository.findByIdWithCheckResults(id)
                .orElseThrow(() -> {
                    log.error("Order not found with id: {}", id);
                    return new ResourceNotFoundException("Cannot find order #" + id);
                });

    }

    @Transactional(readOnly = true)
    public List<RepairOrder> getAllRepairOrders() {
        log.debug("Fetching all repair orders");

        List<RepairOrder> repairOrders = repairOrderRepository.findAll();
        log.debug("Found {} repair orders", repairOrders.size());

        return repairOrders;

    }

    @CacheEvict(value = "orders", key = "#request.id")
    @Transactional
    public RepairOrder updateRepairOrder(UpdateOrderRequest request) {
        log.info("Updating repair order: {}", request.getId());

        RepairOrder existingOrder = getRepairOrderById(request.getId());

        orderMapper.updateOrderFromRequest(request, existingOrder);

        RepairOrder updatedOrder = repairOrderRepository.save(existingOrder);
        log.info("Order {} successfully updated", existingOrder.getId());

        return updatedOrder;
    }

    @CacheEvict(value = "orders", key = "#id")
    @Transactional
    public void deleteRepairOrder(UUID id) {
        log.info("Deleting repair order: {}", id);

        if (!repairOrderRepository.existsById(id)) {
            log.error("Order for deleting not found with id: {}", id);
            throw new ResourceNotFoundException("Cannot find order #" + id);
        }

        repairOrderRepository.deleteById(id);
        log.info("Order {} successfully deleted", id);
    }

    @Transactional
    public void completeOrder(UUID orderId) {
        log.info("Completing order: {}", orderId);

        RepairOrder order = getRepairOrderById(orderId);

        List<CheckResult> postRepairChecks = order.getCheckResults().stream()
                .filter(cr -> "POST_REPAIR".equals(cr.getChecklistType()))
                .toList();

        boolean allCompleted = postRepairChecks.stream()
                .allMatch(cr -> List.of("PASSED", "FAILED", "NOT_APPLICABLE").contains(cr.getStatus()));

        if (!allCompleted) {
            throw new IllegalStateException("Cannot complete order: not all POST_REPAIR checks are completed");
        }

        order.setStatus("COMPLETED");
        repairOrderRepository.save(order);

        log.info("Order {} completed", orderId);
    }


}
