package com.servicecenter.checklist.repository;

import com.servicecenter.checklist.entity.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, UUID> {

    @Query("SELECT ro FROM RepairOrder ro " +
            "LEFT JOIN FETCH ro.checkResults cr " +
            "LEFT JOIN FETCH cr.checkItem " +
            "WHERE ro.id = :id")
    Optional<RepairOrder> findByIdWithCheckResults(@Param("id") UUID id);
}
