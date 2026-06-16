package backend.salud_ocupacional.service;

import backend.salud_ocupacional.dto.HealthRecordDTO;
import backend.salud_ocupacional.model.HealthRecord;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordService {
    HealthRecordDTO registrarToma(String numeroDocumento, HealthRecordDTO registroDTO);

    List<HealthRecordDTO> obtenerHistorialPorEmpleado(String numeroDocumento);

    List<HealthRecord> obtenerRegistrosPorFecha(LocalDate fecha);

    List<HealthRecord> obtenerEntidadesPorDocumento(String numeroDocumento);

    List<HealthRecord> obtenerRegistrosPorRango(LocalDate fechaInicio, LocalDate fechaFin);
}