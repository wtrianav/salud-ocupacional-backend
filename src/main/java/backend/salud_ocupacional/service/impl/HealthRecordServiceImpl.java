package backend.salud_ocupacional.service.impl;

import backend.salud_ocupacional.dto.HealthRecordDTO;
import backend.salud_ocupacional.model.Employee;
import backend.salud_ocupacional.model.HealthRecord;
import backend.salud_ocupacional.repository.EmployeeRepository;
import backend.salud_ocupacional.repository.HealthRecordRepository;
import backend.salud_ocupacional.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public HealthRecordDTO registrarToma(String numeroDocumento, HealthRecordDTO dto) {
        // 1. Buscar al empleado al que le vamos a registrar la salud
        Employee empleado = employeeRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con documento: " + numeroDocumento));

        // 2. Construir la entidad con los datos que vienen del frontend/Postman
        HealthRecord nuevoRegistro = HealthRecord.builder()
                .empleado(empleado)
                .fechaToma(LocalDateTime.now()) // Ponemos la fecha exacta del servidor automáticamente
                .sistolica(dto.sistolica())
                .diastolica(dto.diastolica())
                .pulsacion(dto.pulsacion())
                .saturacion(dto.saturacion())
                .peso(dto.peso())
                .estatura(dto.estatura())
                .build();

        // 3. Guardar en Base de Datos
        HealthRecord registroGuardado = healthRecordRepository.save(nuevoRegistro);

        // 4. Devolver como DTO (Aquí se calculan mágicamente el IMC y las alertas gracias a los @Transient)
        return mapToDTO(registroGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthRecordDTO> obtenerHistorialPorEmpleado(String numeroDocumento) {
        Employee empleado = employeeRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Usamos el método que creamos en el repositorio para traerlos ordenados
        List<HealthRecord> historial = healthRecordRepository.findByEmpleadoIdOrderByFechaTomaDesc(empleado.getId());

        // Convertimos la lista de Entidades a una lista de DTOs usando Streams de Java
        return historial.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar de mapeo
    private HealthRecordDTO mapToDTO(HealthRecord registro) {
        return new HealthRecordDTO(
                registro.getId(),
                registro.getFechaToma(),
                registro.getSistolica(),
                registro.getDiastolica(),
                registro.getPulsacion(),
                registro.getSaturacion(),
                registro.getPeso(),
                registro.getEstatura(),
                registro.getImc(),
                registro.getClasificacionImc(),
                registro.getEstadoTension()
        );
    }
}