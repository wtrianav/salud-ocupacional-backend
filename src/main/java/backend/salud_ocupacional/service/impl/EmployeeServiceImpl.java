package backend.salud_ocupacional.service.impl;

import backend.salud_ocupacional.dto.EmployeeDTO;
import backend.salud_ocupacional.model.Employee;
import backend.salud_ocupacional.repository.EmployeeRepository;
import backend.salud_ocupacional.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public EmployeeDTO registrarEmpleado(EmployeeDTO dto) {
        if (employeeRepository.findByNumeroDocumento(dto.numeroDocumento()).isPresent()) {
            throw new RuntimeException("El empleado con documento " + dto.numeroDocumento() + " ya existe.");
        }

        Employee empleado = Employee.builder()
                .numeroDocumento(dto.numeroDocumento())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .fechaNacimiento(dto.fechaNacimiento())
                .cargo(dto.cargo())
                .dependencia(dto.dependencia())
                .rh(dto.rh())
                .genero(dto.genero())
                .enfermedadesPadecidas(dto.enfermedadesPadecidas())
                .alergias(dto.alergias())
                .operaciones(dto.operaciones())
                .herenciaFamiliar(dto.herenciaFamiliar())
                .build();

        Employee empleadoGuardado = employeeRepository.save(empleado);

        return mapToDTO(empleadoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO buscarPorDocumento(String numeroDocumento) {
        Employee empleado = employeeRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        return mapToDTO(empleado);
    }

    private EmployeeDTO mapToDTO(Employee empleado) {
        return new EmployeeDTO(
                empleado.getId(),
                empleado.getNumeroDocumento(),
                empleado.getNombres(),
                empleado.getApellidos(),
                empleado.getFechaNacimiento(),
                empleado.getCargo(),
                empleado.getDependencia(),
                empleado.getEdadActual(),
                empleado.getRh(),
                empleado.getGenero(),
                empleado.getEnfermedadesPadecidas(),
                empleado.getAlergias(),
                empleado.getOperaciones(),
                empleado.getHerenciaFamiliar()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> listarTodos() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
}