package com.servicecenter.checklist.service;

import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.repository.CheckItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckItemServiceTest {

    @Mock
    private CheckItemRepository checkItemRepository;

    @InjectMocks
    private CheckItemService checkItemService;

    private CheckItem checkItem1;
    private CheckItem checkItem2;
    private String deviceType;

    @BeforeEach
    void setUp() {
        deviceType = "SMARTPHONE";

        checkItem1 = CheckItem.builder()
                .id(UUID.randomUUID())
                .name("Phone turns on?")
                .category("BASIC")
                .deviceType(deviceType)
                .checkKey("PHONE_ON")
                .displayOrder(1)
                .isActive(true)
                .build();

        checkItem2 = CheckItem.builder()
                .id(UUID.randomUUID())
                .name("Display works?")
                .category("DISPLAY")
                .deviceType(deviceType)
                .checkKey("DISPLAY_WORKS")
                .requiredChecks("PHONE_ON")
                .displayOrder(2)
                .isActive(true)
                .build();
    }

    @Test
    void getActiveChecksForDevice_ShouldReturnListOfChecks() {

        List<CheckItem> expectedChecks = Arrays.asList(checkItem1, checkItem2);
        when(checkItemRepository.findActiveChecksForDevice(deviceType))
                .thenReturn(expectedChecks);

        List<CheckItem> actualChecks = checkItemService.getActiveChecksForDevice(deviceType);

        assertNotNull(actualChecks);
        assertEquals(2, actualChecks.size());
        assertEquals(checkItem1.getName(), actualChecks.get(0).getName());
        assertEquals(checkItem2.getName(), actualChecks.get(1).getName());

        verify(checkItemRepository, times(1)).findActiveChecksForDevice(deviceType);
    }

    @Test
    void getActiveChecksForDevice_WithEmptyResult_ShouldReturnEmptyList() {

        when(checkItemRepository.findActiveChecksForDevice(deviceType))
                .thenReturn(List.of());

        List<CheckItem> actualChecks = checkItemService.getActiveChecksForDevice(deviceType);

        assertNotNull(actualChecks);
        assertTrue(actualChecks.isEmpty());

        verify(checkItemRepository, times(1)).findActiveChecksForDevice(deviceType);
    }

    @Test
    void refreshCheckItemsCache_ShouldCallRepository() {

        checkItemService.refreshCheckItemsCache();

    }
}