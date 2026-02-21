package com.servicecenter.checklist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "check_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Check item name is required")
    private String name;

    @Column(name = "category", nullable = false)
    @NotBlank(message = "Category is required")
    private String category;

    @Column(name = "device_type", nullable = false)
    @NotBlank(message = "Device type is required")
    private String deviceType;

    @Column(name = "check_key", unique = true)
    private String checkKey;

    @Column(name = "required_checks")
    private String requiredChecks;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
