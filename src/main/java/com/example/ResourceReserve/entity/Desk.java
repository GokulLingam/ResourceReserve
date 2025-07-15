package com.example.ResourceReserve.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "desks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"floor_id", "desk_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Desk {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;
    
    @Column(name = "desk_number", nullable = false, length = 50)
    private String deskNumber;
    
    @Column(name = "x_position", nullable = false)
    private Integer xPosition;
    
    @Column(name = "y_position", nullable = false)
    private Integer yPosition;
    
    @Column(name = "width", nullable = false)
    @Builder.Default
    private Integer width = 100;
    
    @Column(name = "height", nullable = false)
    @Builder.Default
    private Integer height = 100;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private DeskStatus status = DeskStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "desk_type", nullable = false, length = 50)
    @Builder.Default
    private DeskType deskType = DeskType.STANDARD;
    
    @Column(name = "equipment", columnDefinition = "NVARCHAR(MAX)")
    private String equipment; // JSON string storing equipment information
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum DeskStatus {
        AVAILABLE, OCCUPIED, RESERVED, MAINTENANCE
    }
    
    public enum DeskType {
        STANDARD, STANDING, COLLABORATIVE
    }
} 