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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        Employee empleado = employeeRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con documento: " + numeroDocumento));

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

        HealthRecord registroGuardado = healthRecordRepository.save(nuevoRegistro);

        return mapToDTO(registroGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthRecordDTO> obtenerHistorialPorEmpleado(String numeroDocumento) {
        Employee empleado = employeeRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        List<HealthRecord> historial = healthRecordRepository.findByEmpleadoIdOrderByFechaTomaDesc(empleado.getId());

        return historial.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthRecord> obtenerRegistrosPorFecha(LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay(); // 2026-05-15T00:00:00
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX); // 2026-05-15T23:59:59.999999999

        return healthRecordRepository.findByFechaTomaBetween(inicioDia, finDia);
    }

    @Override
    public List<HealthRecord> obtenerEntidadesPorDocumento(String numeroDocumento) {
        return healthRecordRepository.findByEmpleado_NumeroDocumentoOrderByFechaTomaAsc(numeroDocumento);
    }

    @Override
    public List<HealthRecord> obtenerRegistrosPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        return healthRecordRepository.findByFechaTomaBetween(inicio, fin);
    }

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