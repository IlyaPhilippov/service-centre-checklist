package com.servicecenter.checklist.service;

import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.entity.CheckResult;
import com.servicecenter.checklist.entity.RepairOrder;
import com.servicecenter.checklist.repository.CheckResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CheckResultServiceTest {

    @Mock
    private CheckResultRepository checkResultRepository;

    @InjectMocks
    private CheckResultService checkResultService;

    @Captor
    private ArgumentCaptor<CheckResult> checkResultCaptor;

    private RepairOrder repairOrder;
    private CheckItem checkItem1;
    private CheckItem checkItem2;
    private CheckResult checkResult;
    private UUID resultId;

    @BeforeEach
    void setUp() {
        repairOrder = RepairOrder.builder()
                .id(UUID.randomUUID())
                .clientName("Test Client")
                .deviceType("SMARTPHONE")
                .build();

        checkItem1 = CheckItem.builder()
                .id(UUID.randomUUID())
                .name("Phone turns on?")
                .checkKey("PHONE_ON")
                .build();

        checkItem2 = CheckItem.builder()
                .id(UUID.randomUUID())
                .name("Display works?")
                .checkKey("DISPLAY_WORKS")
                .requiredChecks("PHONE_ON")
                .build();

        resultId = UUID.randomUUID();
        checkResult = CheckResult.builder()
                .id(resultId)
                .repairOrder(repairOrder)
                .checkItem(checkItem1)
                .checklistType("PRE_REPAIR")
                .status("PENDING")
                .build();
    }

    @Test
    void createPreRepairChecklist_ShouldSaveAllCheckItems() {

        List<CheckItem> checkItems = Arrays.asList(checkItem1, checkItem2);

        checkResultService.createPreRepairChecklist(repairOrder, checkItems);

        verify(checkResultRepository, times(2)).save(checkResultCaptor.capture());

        List<CheckResult> savedResults = checkResultCaptor.getAllValues();
        assertEquals(2, savedResults.size());

        CheckResult first = savedResults.get(0);
        assertEquals(repairOrder.getId(), first.getRepairOrder().getId());
        assertEquals(checkItem1.getId(), first.getCheckItem().getId());
        assertEquals("PRE_REPAIR", first.getChecklistType());
        assertEquals("PENDING", first.getStatus());

        CheckResult second = savedResults.get(1);
        assertEquals(checkItem2.getId(), second.getCheckItem().getId());
    }

    @Test
    void createPostRepairChecklist_ShouldDeleteOldAndSaveNew() {

        List<CheckItem> checkItems = Arrays.asList(checkItem1, checkItem2);

        checkResultService.createPostRepairChecklist(repairOrder, checkItems);

        verify(checkResultRepository, times(1))
                .deleteByRepairOrderAndChecklistType(repairOrder, "POST_REPAIR");
        verify(checkResultRepository, times(2)).save(any(CheckResult.class));
    }

    @Test
    void updateStatus_ShouldUpdateStatusAndCheckedAt() {

        String newStatus = "PASSED";
        when(checkResultRepository.findById(resultId))
                .thenReturn(Optional.of(checkResult));
        when(checkResultRepository.save(any(CheckResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CheckResult updated = checkResultService.updateStatus(resultId, newStatus);

        assertNotNull(updated);
        assertEquals(newStatus, updated.getStatus());
        assertNotNull(updated.getCheckedAt());

        verify(checkResultRepository, times(1)).findById(resultId);
        verify(checkResultRepository, times(1)).save(checkResult);
    }

    @Test
    void updateStatus_WithInvalidId_ShouldThrowException() {

        UUID invalidId = UUID.randomUUID();
        when(checkResultRepository.findById(invalidId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                checkResultService.updateStatus(invalidId, "PASSED")
        );

        verify(checkResultRepository, times(1)).findById(invalidId);
        verify(checkResultRepository, never()).save(any());
    }

    @Test
    void disableDependentChecks_WhenPhoneFails_ShouldSetDisplayToNotApplicable() {
        checkResult.setStatus("FAILED");

        CheckResult displayResult = CheckResult.builder()
                .id(UUID.randomUUID())
                .repairOrder(repairOrder)
                .checkItem(checkItem2)
                .checklistType("PRE_REPAIR")
                .status("PENDING")
                .build();

        List<CheckResult> allChecks = Arrays.asList(checkResult, displayResult);

        when(checkResultRepository.findByRepairOrderAndChecklistType(repairOrder, "PRE_REPAIR"))
                .thenReturn(allChecks);
        when(checkResultRepository.findById(resultId))
                .thenReturn(Optional.of(checkResult));
        when(checkResultRepository.save(any(CheckResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        checkResultService.updateStatus(resultId, "FAILED");

        verify(checkResultRepository, times(1)).save(checkResultCaptor.capture());

    }
}