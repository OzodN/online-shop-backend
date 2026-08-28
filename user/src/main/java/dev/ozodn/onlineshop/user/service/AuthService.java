package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.user.dto.RegisterRequest;
import dev.ozodn.onlineshop.user.dto.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);
}
