package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.User;
import com.example.ResourceReserve.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByRole(UserRole role);
    
    List<User> findByDepartment(String department);
} 