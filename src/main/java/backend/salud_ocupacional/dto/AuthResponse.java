package backend.salud_ocupacional.dto;

public record AuthResponse(
        String token,
        String username,
        String rol
) {}
