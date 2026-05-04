package backend.salud_ocupacional.dto;

import java.time.LocalDateTime;

public record HealthRecordDTO(
        Long id,
        LocalDateTime fechaToma,
        Integer sistolica,
        Integer diastolica,
        Integer pulsacion,
        Integer saturacion,
        Double peso,
        Double estatura,
        Double imc,
        String clasificacionImc,
        String estadoTension
) {}