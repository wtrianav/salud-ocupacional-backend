package backend.salud_ocupacional.controller;

import backend.salud_ocupacional.dto.EmployeeDTO;
import backend.salud_ocupacional.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.salud_ocupacional.model.Employee;
import backend.salud_ocupacional.repository.EmployeeRepository;
import org.springframework.web.server.ResponseStatusException;
import backend.salud_ocupacional.service.GeminiService;
import backend.salud_ocupacional.model.HealthRecord;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final GeminiService geminiService;

    @PostMapping
    public ResponseEntity<EmployeeDTO> registrar(@RequestBody EmployeeDTO empleadoDTO) {
        EmployeeDTO nuevoEmpleado = employeeService.registrarEmpleado(empleadoDTO);
        return new ResponseEntity<>(nuevoEmpleado, HttpStatus.CREATED);
    }

    @GetMapping("/{documento}")
    public ResponseEntity<EmployeeDTO> buscarPorDocumento(@PathVariable String documento) {
        EmployeeDTO empleado = employeeService.buscarPorDocumento(documento);
        return ResponseEntity.ok(empleado);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> listarTodos() {
        return ResponseEntity.ok(employeeService.listarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmployeeDTO dto) {
        Employee empleado = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        empleado.setNombres(dto.nombres());
        empleado.setApellidos(dto.apellidos());
        empleado.setCargo(dto.cargo());
        empleado.setDependencia(dto.dependencia());
        empleado.setFechaNacimiento(dto.fechaNacimiento());
        empleado.setRh(dto.rh());

        empleado.setGenero(dto.genero());
        empleado.setEnfermedadesPadecidas(dto.enfermedadesPadecidas());
        empleado.setAlergias(dto.alergias());
        empleado.setOperaciones(dto.operaciones());
        empleado.setHerenciaFamiliar(dto.herenciaFamiliar());

        Employee actualizado = employeeRepository.save(empleado);

        EmployeeDTO respuesta = new EmployeeDTO(
                actualizado.getId(),
                actualizado.getNumeroDocumento(),
                actualizado.getNombres(),
                actualizado.getApellidos(),
                actualizado.getFechaNacimiento(),
                actualizado.getCargo(),
                actualizado.getDependencia(),
                actualizado.getEdadActual(),
                actualizado.getRh(),
                actualizado.getGenero(),
                actualizado.getEnfermedadesPadecidas(),
                actualizado.getAlergias(),
                actualizado.getOperaciones(),
                actualizado.getHerenciaFamiliar()
        );

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{documento}/diagnostico-ia")
    public ResponseEntity<Map<String, String>> obtenerDiagnosticoIA(@PathVariable String documento) {
        Employee empleado = employeeRepository.findByNumeroDocumento(documento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));

        List<HealthRecord> historial = empleado.getHistoricoSalud();
        HealthRecord ultimoRegistro = historial.isEmpty() ? null : historial.get(historial.size() - 1);

        String diagnostico = geminiService.generarDiagnostico(empleado, ultimoRegistro);

        return ResponseEntity.ok(Map.of("diagnostico", diagnostico));
    }
}