package com.servicecenter.checklist.repository;

import com.servicecenter.checklist.dto.response.CheckItemResponse;
import com.servicecenter.checklist.entity.CheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CheckItemRepository extends JpaRepository<CheckItem, UUID> {

    @Query("SELECT c FROM CheckItem c " +
            "WHERE c.deviceType = :deviceType " +
            "AND c.isActive = true " +
            "ORDER BY c.displayOrder")
    List<CheckItem> findActiveChecksForDevice(@Param("deviceType") String deviceType);
}
