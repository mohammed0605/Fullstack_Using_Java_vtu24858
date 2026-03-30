package com.eventplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Long userId;
    private String message;

    public AuthResponse(String message) {
        this.message = message;
    }
}
