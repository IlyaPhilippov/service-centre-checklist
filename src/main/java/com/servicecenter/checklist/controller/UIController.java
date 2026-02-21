package com.servicecenter.checklist.controller;

import com.servicecenter.checklist.dto.request.CreateOrderRequest;
import com.servicecenter.checklist.dto.response.OrderResponse;
import com.servicecenter.checklist.service.CheckResultService;
import com.servicecenter.checklist.service.RepairOrderService;
import com.servicecenter.checklist.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UIController {

    private final RepairOrderService repairOrderService;
    private final CheckResultService checkResultService;
    private final OrderMapper orderMapper;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("orders", repairOrderService.getAllRepairOrders());
        return "index";
    }

    @GetMapping("/orders/new")
    public String newOrderForm(Model model) {
        model.addAttribute("order", new CreateOrderRequest());
        return "order-form";
    }

    @PostMapping("/orders")
    public String createOrder(CreateOrderRequest request) {
        var order = orderMapper.toEntity(request);
        var savedOrder = repairOrderService.createRepairOrder(order);
        return "redirect:/ui/orders/" + savedOrder.getId();
    }

    @GetMapping("/orders/{id}")
    public String viewOrder(@PathVariable UUID id, Model model) {
        var order = repairOrderService.getRepairOrderById(id);
        model.addAttribute("order", orderMapper.toResponse(order));
        return "order-view";
    }

    @PostMapping("/check-results/{id}/status")
    public String updateStatusFromForm(@PathVariable UUID id,
                                       @RequestParam String status,
                                       @RequestParam UUID orderId) {
        log.info("Form request to update check result {} to status {}", id, status);

        checkResultService.updateStatus(id, status);

        return "redirect:/ui/orders/" + orderId;
    }

    @PostMapping("/orders/{id}/post-repair")
    public String createPostRepairChecklist(@PathVariable UUID id) {
        log.info("Creating POST_REPAIR checklist for order: {}", id);

        repairOrderService.createPostRepairChecklist(id);

        return "redirect:/ui/orders/" + id;
    }

    @PostMapping("/orders/{id}/complete")
    public String completeOrder(@PathVariable UUID id) {
        log.info("Completing order: {}", id);

        try {
            repairOrderService.completeOrder(id);
            return "redirect:/ui/";
        } catch (IllegalStateException e) {
            log.error("Cannot complete order: {}", e.getMessage());
            return "redirect:/ui/orders/" + id + "?error=notCompleted";
        }
    }
}