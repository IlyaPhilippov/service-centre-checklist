package com.servicecenter.checklist.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "check_results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_order_id", nullable = false)
    @NotNull(message = "Repair order is required")
    private RepairOrder repairOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_item_id", nullable = false)
    @NotNull(message = "Check item is required")
    private CheckItem checkItem;

    @Column(name = "checklist_type", nullable = false)
    @NotBlank(message = "Checklist type is  required")
    private String checklistType;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;
}
