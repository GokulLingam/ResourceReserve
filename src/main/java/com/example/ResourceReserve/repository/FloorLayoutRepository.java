package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.FloorLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FloorLayoutRepository extends JpaRepository<FloorLayout, UUID> {
    
    @Query("SELECT fl FROM FloorLayout fl WHERE fl.floor.id = :floorId AND fl.isActive = true")
    Optional<FloorLayout> findByFloorIdAndIsActive(@Param("floorId") UUID floorId);
    
    @Query("SELECT fl FROM FloorLayout fl WHERE fl.floor.id = :floorId ORDER BY fl.version DESC")
    Optional<FloorLayout> findLatestByFloorId(@Param("floorId") UUID floorId);
    
    @Query("SELECT fl FROM FloorLayout fl WHERE fl.floor.id = :floorId AND fl.version = :version")
    Optional<FloorLayout> findByFloorIdAndVersion(@Param("floorId") UUID floorId, @Param("version") Integer version);
} 