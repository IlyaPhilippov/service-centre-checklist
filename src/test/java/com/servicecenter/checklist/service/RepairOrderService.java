package com.servicecenter.checklist.service;

import com.servicecenter.checklist.dto.request.UpdateOrderRequest;
import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.entity.RepairOrder;
import com.servicecenter.checklist.exception.ResourceNotFoundException;
import com.servicecenter.checklist.mapper.OrderMapper;
import com.servicecenter.checklist.repository.RepairOrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @Mock
    private CheckItemService checkItemService;

    @Mock
    private CheckResultService checkResultService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RepairOrderService repairOrderService;

    private RepairOrder repairOrder;
    private UUID orderId;
    private List<CheckItem> checkItems;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        repairOrder = RepairOrder.builder()
                .id(orderId)
                .clientName("Test Client")
                .deviceType("SMARTPHONE")
                .deviceBrand("Apple")
                .deviceModel("iPhone 11")
                .createdAt(LocalDateTime.now())
                .status("IN_PROGRESS")
                .build();

        CheckItem checkItem1 = CheckItem.builder()
                .id(UUID.randomUUID())
                .name("Phone turns on?")
                .build();

        CheckItem checkItem2 = CheckItem.builder()
                .id(UUID.randomUUID())
                .name("Display works?")
                .build();

        checkItems = Arrays.asList(checkItem1, checkItem2);
    }

    @Test
    void createRepairOrder_ShouldSaveOrderAndCreateChecklist() {
        
        when(repairOrderRepository.save(any(RepairOrder.class)))
                .thenReturn(repairOrder);
        when(checkItemService.getActiveChecksForDevice(repairOrder.getDeviceType()))
                .thenReturn(checkItems);
        when(repairOrderRepository.findByIdWithCheckResults(orderId))
                .thenReturn(Optional.of(repairOrder));

        RepairOrder result = repairOrderService.createRepairOrder(repairOrder);

        assertNotNull(result);
        assertEquals(repairOrder.getId(), result.getId());

        verify(repairOrderRepository, times(1)).save(repairOrder);
        verify(checkItemService, times(1)).getActiveChecksForDevice(repairOrder.getDeviceType());
        verify(checkResultService, times(1)).createPreRepairChecklist(repairOrder, checkItems);
        verify(repairOrderRepository, times(1)).flush();
        verify(entityManager, times(1)).clear();
        verify(repairOrderRepository, times(1)).findByIdWithCheckResults(orderId);
    }

    @Test
    void getRepairOrderById_WhenExists_ShouldReturnOrder() {
        
        when(repairOrderRepository.findByIdWithCheckResults(orderId))
                .thenReturn(Optional.of(repairOrder));

        RepairOrder result = repairOrderService.getRepairOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals("Test Client", result.getClientName());

        verify(repairOrderRepository, times(1)).findByIdWithCheckResults(orderId);
    }

    @Test
    void getRepairOrderById_WhenNotExists_ShouldThrowException() {
        
        when(repairOrderRepository.findByIdWithCheckResults(orderId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                repairOrderService.getRepairOrderById(orderId)
        );

        verify(repairOrderRepository, times(1)).findByIdWithCheckResults(orderId);
    }

    @Test
    void updateRepairOrder_ShouldUpdateOnlyProvidedFields() {
        
        UpdateOrderRequest request = UpdateOrderRequest.builder()
                .id(orderId)
                .clientName("Updated Name")
                .deviceModel("iPhone 12")
                .build();

        when(repairOrderRepository.findByIdWithCheckResults(orderId))
                .thenReturn(Optional.of(repairOrder));
        when(repairOrderRepository.save(any(RepairOrder.class)))
                .thenReturn(repairOrder);

        RepairOrder result = repairOrderService.updateRepairOrder(request);

        assertNotNull(result);
        verify(orderMapper, times(1)).updateOrderFromRequest(request, repairOrder);
        verify(repairOrderRepository, times(1)).save(repairOrder);
    }

    @Test
    void deleteRepairOrder_WhenExists_ShouldDelete() {
        
        when(repairOrderRepository.existsById(orderId)).thenReturn(true);
        doNothing().when(repairOrderRepository).deleteById(orderId);
        
        repairOrderService.deleteRepairOrder(orderId);
        
        verify(repairOrderRepository, times(1)).existsById(orderId);
        verify(repairOrderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void deleteRepairOrder_WhenNotExists_ShouldThrowException() {
        
        when(repairOrderRepository.existsById(orderId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                repairOrderService.deleteRepairOrder(orderId)
        );

        verify(repairOrderRepository, times(1)).existsById(orderId);
        verify(repairOrderRepository, never()).deleteById(any());
    }
}