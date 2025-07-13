package com.example.ResourceReserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {
    
    private Boolean success;
    private String token;
    private String refreshToken;
    private Integer expiresIn;
} 