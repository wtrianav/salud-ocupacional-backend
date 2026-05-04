package backend.salud_ocupacional.controller;

import backend.salud_ocupacional.dto.HealthRecordDTO;
import backend.salud_ocupacional.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final HealthRecordRepository healthRecordRepository;

    @GetMapping("/registros")
    public ResponseEntity<List<HealthRecordDTO>> obtenerTodosLosRegistros() {
        List<HealthRecordDTO> todosLosRegistros = healthRecordRepository.findAll().stream()
                .map(registro -> new HealthRecordDTO(
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
                )).toList();

        return ResponseEntity.ok(todosLosRegistros);
    }
}
