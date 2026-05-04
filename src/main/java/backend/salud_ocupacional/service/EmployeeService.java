package backend.salud_ocupacional.service;

import backend.salud_ocupacional.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeDTO registrarEmpleado(EmployeeDTO empleadoDTO);
    EmployeeDTO buscarPorDocumento(String numeroDocumento);
    List<EmployeeDTO> listarTodos();
}