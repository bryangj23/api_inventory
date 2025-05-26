package com.nexos.api_inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@MappedSuperclass
public class BaseDateEntity implements Serializable {

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        setCreatedAt(nowUtc);
        setUpdatedAt(nowUtc);
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }
}
