package com.servicecenter.checklist.repository;

import com.servicecenter.checklist.entity.CheckResult;
import com.servicecenter.checklist.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CheckResultRepository extends JpaRepository<CheckResult, UUID> {

    List<CheckResult> findByRepairOrderAndChecklistType(RepairOrder repairOrder, String checklistType);

    void deleteByRepairOrderAndChecklistType(RepairOrder repairOrder, String checklistType);
}
