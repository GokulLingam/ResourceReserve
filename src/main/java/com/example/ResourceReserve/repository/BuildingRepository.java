package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<Building, UUID> {
    
    @Query("SELECT b FROM Building b ORDER BY b.name")
    List<Building> findAllOrderByName();
    
    @Query("SELECT b FROM Building b WHERE b.city = ?1 ORDER BY b.name")
    List<Building> findByCityOrderByName(String city);
    
    @Query("SELECT b FROM Building b WHERE b.country = ?1 ORDER BY b.name")
    List<Building> findByCountryOrderByName(String country);
} 