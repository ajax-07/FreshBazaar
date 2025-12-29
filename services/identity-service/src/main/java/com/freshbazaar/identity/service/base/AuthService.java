package com.freshbazaar.identity.service.base;

import com.freshbazaar.identity.dto.AuthResponse;
import com.freshbazaar.identity.dto.LoginRequest;
import com.freshbazaar.identity.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
