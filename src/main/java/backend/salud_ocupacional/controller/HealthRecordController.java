package backend.salud_ocupacional.controller;

import backend.salud_ocupacional.dto.HealthRecordDTO;
import backend.salud_ocupacional.model.HealthRecord;
import backend.salud_ocupacional.service.HealthRecordService;
import backend.salud_ocupacional.repository.HealthRecordRepository;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados/{documento}/registros")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final HealthRecordRepository healthRecordRepository;

    @PostMapping
    public ResponseEntity<HealthRecordDTO> registrarToma(@PathVariable String documento, @RequestBody HealthRecordDTO registroDTO) {
        HealthRecordDTO nuevoRegistro = healthRecordService.registrarToma(documento, registroDTO);
        return new ResponseEntity<>(nuevoRegistro, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HealthRecordDTO>> obtenerHistorial(@PathVariable String documento) {
        List<HealthRecordDTO> historial = healthRecordService.obtenerHistorialPorEmpleado(documento);
        return ResponseEntity.ok(historial);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthRecordDTO> actualizarToma(@PathVariable String documento, @PathVariable Long id, @RequestBody HealthRecordDTO dto) {
        HealthRecord registro = healthRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro médico no encontrado"));

        registro.setSistolica(dto.sistolica());
        registro.setDiastolica(dto.diastolica());
        registro.setPulsacion(dto.pulsacion());
        registro.setSaturacion(dto.saturacion());
        registro.setPeso(dto.peso());
        registro.setEstatura(dto.estatura());

        HealthRecord actualizado = healthRecordRepository.save(registro);

        HealthRecordDTO respuesta = new HealthRecordDTO(
                actualizado.getId(),
                actualizado.getFechaToma(),
                actualizado.getSistolica(),
                actualizado.getDiastolica(),
                actualizado.getPulsacion(),
                actualizado.getSaturacion(),
                actualizado.getPeso(),
                actualizado.getEstatura(),
                null,
                null,
                null
        );

        return ResponseEntity.ok(respuesta);
    }
}