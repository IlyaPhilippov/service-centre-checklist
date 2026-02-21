package com.servicecenter.checklist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "repair_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_name", nullable = false)
    @NotBlank(message = "Client name is required")
    @Size(max = 255, message = "Client name must be less than 255 characters")
    private String clientName;

    @Column(name = "device_type", nullable = false)
    @NotBlank(message = "Device type is required")
    private String deviceType;

    @Column(name = "device_brand", nullable = false)
    @NotBlank(message = "Device brand is required")
    @Size (max = 50)
    private String deviceBrand;

    @Column(name = "device_model", nullable = false)
    @NotBlank(message = "Device model is required")
    @Size(max = 100)
    private String deviceModel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CheckResult> checkResults = new ArrayList<>();

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
