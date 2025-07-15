package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeskRepository extends JpaRepository<Desk, UUID> {
    
    @Query("SELECT d FROM Desk d WHERE d.floor.id = :floorId ORDER BY d.deskNumber")
    List<Desk> findByFloorIdOrderByDeskNumber(@Param("floorId") UUID floorId);
    
    @Query("SELECT d FROM Desk d WHERE d.floor.id = :floorId AND d.isActive = :isActive ORDER BY d.deskNumber")
    List<Desk> findByFloorIdAndIsActiveOrderByDeskNumber(@Param("floorId") UUID floorId, @Param("isActive") Boolean isActive);
    
    @Query("SELECT d FROM Desk d WHERE d.floor.id = :floorId AND d.status = :status AND d.isActive = true ORDER BY d.deskNumber")
    List<Desk> findByFloorIdAndStatusAndIsActiveOrderByDeskNumber(@Param("floorId") UUID floorId, @Param("status") Desk.DeskStatus status);
    
    @Query("SELECT d FROM Desk d WHERE d.floor.id = :floorId AND d.deskType = :deskType AND d.isActive = true ORDER BY d.deskNumber")
    List<Desk> findByFloorIdAndDeskTypeAndIsActiveOrderByDeskNumber(@Param("floorId") UUID floorId, @Param("deskType") Desk.DeskType deskType);
    
    @Query("SELECT d FROM Desk d WHERE d.floor.id = :floorId AND d.deskNumber = :deskNumber")
    Optional<Desk> findByFloorIdAndDeskNumber(@Param("floorId") UUID floorId, @Param("deskNumber") String deskNumber);
    
    @Query("SELECT d FROM Desk d WHERE d.status = :status AND d.isActive = true ORDER BY d.floor.building.name, d.floor.floorNumber, d.deskNumber")
    List<Desk> findByStatusAndIsActiveOrderByBuildingAndFloorAndDesk(@Param("status") Desk.DeskStatus status);
    
    @Query("SELECT d FROM Desk d WHERE d.deskType = :deskType AND d.isActive = true ORDER BY d.floor.building.name, d.floor.floorNumber, d.deskNumber")
    List<Desk> findByDeskTypeAndIsActiveOrderByBuildingAndFloorAndDesk(@Param("deskType") Desk.DeskType deskType);
    
    @Query("SELECT COUNT(d) FROM Desk d WHERE d.floor.id = :floorId AND d.isActive = true")
    Long countActiveDesksByFloorId(@Param("floorId") UUID floorId);
    
    @Query("SELECT COUNT(d) FROM Desk d WHERE d.floor.id = :floorId AND d.status = :status AND d.isActive = true")
    Long countByFloorIdAndStatusAndIsActive(@Param("floorId") UUID floorId, @Param("status") Desk.DeskStatus status);
} 