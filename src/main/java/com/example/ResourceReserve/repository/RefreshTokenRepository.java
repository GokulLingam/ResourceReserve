package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.RefreshToken;
import com.example.ResourceReserve.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByTokenAndIsRevokedFalse(String token);
    
    void deleteByUser(User user);
    
    void deleteByUserAndIsRevokedTrue(User user);
} 