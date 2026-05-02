package com.juegos1000tres.juegos1000tres_backend.auth;

public record AuthUserResponse(String nombre, String email, String role) {

    public static AuthUserResponse from(AuthUser user) {
        return new AuthUserResponse(user.nombre(), user.email(), user.role().name());
    }
}
