package backend.salud_ocupacional.service;

import backend.salud_ocupacional.dto.HealthRecordDTO;
import java.util.List;

public interface HealthRecordService {
    // Usamos el número de documento del empleado para buscarlo más fácil
    HealthRecordDTO registrarToma(String numeroDocumento, HealthRecordDTO registroDTO);

    // Obtener todo el historial de un empleado
    List<HealthRecordDTO> obtenerHistorialPorEmpleado(String numeroDocumento);
}