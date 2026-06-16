package backend.salud_ocupacional.controller;

import backend.salud_ocupacional.model.Employee;
import backend.salud_ocupacional.model.HealthRecord;
import backend.salud_ocupacional.repository.EmployeeRepository;
import backend.salud_ocupacional.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final HealthRecordRepository healthRecordRepository;

    @GetMapping("/resumen")
    @Transactional(readOnly = true) // <-- 1. Mantiene la base de datos abierta de forma segura
    public ResponseEntity<Map<String, Object>> getDashboardResumen() {
        Map<String, Object> resumen = new HashMap<>();

        List<Employee> empleadosRaw = employeeRepository.findAll();
        List<Map<String, Object>> empleadosSeguros = new ArrayList<>();

        for (Employee emp : empleadosRaw) {
            Map<String, Object> eMap = new HashMap<>();
            eMap.put("id", emp.getId());
            eMap.put("numeroDocumento", emp.getNumeroDocumento());
            eMap.put("nombres", emp.getNombres());
            eMap.put("apellidos", emp.getApellidos());
            eMap.put("dependencia", emp.getDependencia());
            eMap.put("cargo", emp.getCargo());
            try { eMap.put("edadActual", emp.getEdadActual()); } catch(Exception e) {}

            empleadosSeguros.add(eMap);
        }

        List<HealthRecord> todosLosRegistros = healthRecordRepository.findAll();
        Map<Long, HealthRecord> ultimasTomasMap = new HashMap<>();

        for (HealthRecord registro : todosLosRegistros) {
            if (registro.getEmpleado() == null) continue; // Protección extra

            Long empId = registro.getEmpleado().getId();
            if (!ultimasTomasMap.containsKey(empId)) {
                ultimasTomasMap.put(empId, registro);
            } else {
                HealthRecord actual = ultimasTomasMap.get(empId);
                if (registro.getFechaToma() != null && actual.getFechaToma() != null &&
                        registro.getFechaToma().isAfter(actual.getFechaToma())) {
                    ultimasTomasMap.put(empId, registro);
                }
            }
        }

        List<Map<String, Object>> ultimasTomasSeguras = new ArrayList<>();
        for (HealthRecord hr : ultimasTomasMap.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hr.getId());
            map.put("sistolica", hr.getSistolica());
            map.put("diastolica", hr.getDiastolica());
            map.put("pulsacion", hr.getPulsacion());
            map.put("saturacion", hr.getSaturacion());
            map.put("peso", hr.getPeso());
            map.put("estatura", hr.getEstatura());
            map.put("imc", hr.getImc());
            map.put("clasificacionImc", hr.getClasificacionImc());
            map.put("estadoTension", hr.getEstadoTension());
            map.put("fechaToma", hr.getFechaToma() != null ? hr.getFechaToma().toString() : null);

            Map<String, Object> emp = new HashMap<>();
            emp.put("id", hr.getEmpleado().getId());
            emp.put("numeroDocumento", hr.getEmpleado().getNumeroDocumento());
            emp.put("nombres", hr.getEmpleado().getNombres());
            emp.put("apellidos", hr.getEmpleado().getApellidos());
            emp.put("dependencia", hr.getEmpleado().getDependencia());
            emp.put("cargo", hr.getEmpleado().getCargo());

            map.put("empleado", emp);
            ultimasTomasSeguras.add(map);
        }

        resumen.put("empleados", empleadosSeguros);
        resumen.put("totalRegistros", todosLosRegistros.size());
        resumen.put("ultimasTomas", ultimasTomasSeguras);

        return ResponseEntity.ok(resumen);
    }
}