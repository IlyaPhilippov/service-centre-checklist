package com.servicecenter.checklist.service;

import com.servicecenter.checklist.entity.CheckItem;
import com.servicecenter.checklist.entity.CheckResult;
import com.servicecenter.checklist.entity.RepairOrder;
import com.servicecenter.checklist.repository.CheckResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckResultService {

    private final CheckResultRepository checkResultRepository;

    @Transactional
    public void createPreRepairChecklist(RepairOrder repairOrder, List<CheckItem> checkItems) {
        log.info("Creating PRE_REPAIR checklist for order: {}", repairOrder.getId());

        for (CheckItem item : checkItems) {
            CheckResult checkResult = CheckResult.builder()
                    .repairOrder(repairOrder)
                    .checkItem(item)
                    .checklistType("PRE_REPAIR")
                    .status("PENDING")
                    .build();
            checkResultRepository.save(checkResult);
        }

        log.info("PRE_REPAIR checklist created with {} items", checkItems.size());
    }

    @Transactional
    public void createPostRepairChecklist(RepairOrder repairOrder, List<CheckItem> checkItems) {
        log.info("Creating POST_REPAIR checklist for order: {}", repairOrder.getId());

        checkResultRepository.deleteByRepairOrderAndChecklistType(repairOrder, "POST_REPAIR");

        for (CheckItem item : checkItems) {
            CheckResult checkResult = CheckResult.builder()
                    .repairOrder(repairOrder)
                    .checkItem(item)
                    .checklistType("POST_REPAIR")
                    .status("PENDING")
                    .build();
            checkResultRepository.save(checkResult);
        }

        log.info("POST_REPAIR checklist created with {} items", checkItems.size());
    }

    @Transactional
    public CheckResult updateStatus(UUID resultId, String newStatus) {
        log.info("Updating check result {} to status: {}", resultId, newStatus);

        CheckResult checkResult = checkResultRepository.findById(resultId)
                .orElseThrow(() -> {
                    log.error("Check result not found: {}", resultId);
                    return new RuntimeException("Check result not found");
                });

        String oldStatus = checkResult.getStatus();
        checkResult.setStatus(newStatus);
        checkResult.setCheckedAt(LocalDateTime.now());

        if ("FAILED".equals(newStatus) && !"FAILED".equals(oldStatus)) {
            disableDependentChecks(checkResult);
        }

        CheckResult updated = checkResultRepository.save(checkResult);
        log.info("Check result {} updated to {}", resultId, newStatus);

        return updated;
    }

    private void disableDependentChecks(CheckResult failedCheck) {
        String failedKey = failedCheck.getCheckItem().getCheckKey();
        if (failedKey == null) {
            log.debug("Check {} has no key, no dependencies to disable", failedCheck.getCheckItem().getName());
            return;
        }

        log.debug("Checking dependencies for failed key: {}", failedKey);

        List<CheckResult> allChecks = checkResultRepository
                .findByRepairOrderAndChecklistType(
                        failedCheck.getRepairOrder(),
                        failedCheck.getChecklistType()
                );

        for (CheckResult check : allChecks) {
            String required = check.getCheckItem().getRequiredChecks();
            if (required == null || required.isBlank()) continue;

            Set<String> requiredKeys = Arrays.stream(required.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

            if (requiredKeys.contains(failedKey)) {
                if (!"NOT_APPLICABLE".equals(check.getStatus())) {
                    log.debug("Setting {} to NOT_APPLICABLE because {} failed",
                            check.getCheckItem().getName(), failedKey);
                    check.setStatus("NOT_APPLICABLE");
                    check.setCheckedAt(LocalDateTime.now());
                    checkResultRepository.save(check);
                }
            }
        }
    }

    public List<CheckResult> getChecklist(UUID orderId, String type) {
        log.debug("Fetching {} checklist for order: {}", type, orderId);
        // TODO: получить заказ и найти его результаты
        return null;
    }
}