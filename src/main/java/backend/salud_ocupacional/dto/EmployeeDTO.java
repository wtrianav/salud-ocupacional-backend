package backend.salud_ocupacional.dto;

import backend.salud_ocupacional.model.enums.TipoSangre;
import backend.salud_ocupacional.model.enums.Genero;

import java.time.LocalDate;

public record EmployeeDTO(
        Long id,
        String numeroDocumento,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String cargo,
        String dependencia,
        Integer edadActual,
        TipoSangre rh,
        Genero genero,
        String enfermedadesPadecidas,
        String alergias,
        String operaciones,
        String herenciaFamiliar
) {}