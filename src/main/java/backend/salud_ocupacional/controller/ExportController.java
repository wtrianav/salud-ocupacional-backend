package backend.salud_ocupacional.controller;

import backend.salud_ocupacional.model.HealthRecord;
import backend.salud_ocupacional.service.ExportService;
import backend.salud_ocupacional.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/exportar")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportacionService;
    private final HealthRecordService healthRecordService;

    @GetMapping("/diario")
    public ResponseEntity<InputStreamResource> exportarReporteDiario(@RequestParam String fecha) throws IOException {
        LocalDate fechaFiltro = LocalDate.parse(fecha);

        List<HealthRecord> registrosDelDia = healthRecordService.obtenerRegistrosPorFecha(fechaFiltro);

        ByteArrayInputStream in = exportacionService.generarReporteDiarioExcel(registrosDelDia, fechaFiltro);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Reporte_Salud_Ocupacional_" + fecha + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/expediente/{documento}")
    public ResponseEntity<InputStreamResource> exportarExpedienteEmpleado(@PathVariable String documento) throws IOException {
        List<HealthRecord> historial = healthRecordService.obtenerEntidadesPorDocumento(documento);
        ByteArrayInputStream in = exportacionService.generarExpedienteEmpleadoExcel(historial, documento);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Expediente_Medico_" + documento + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/rango")
    public ResponseEntity<InputStreamResource> exportarReportePorRango(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) throws IOException {

        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        List<HealthRecord> registros = healthRecordService.obtenerRegistrosPorRango(inicio, fin);
        ByteArrayInputStream in = exportacionService.generarReportePorRangoExcel(registros, inicio, fin);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Consolidado_Tomas_" + fechaInicio + "_al_" + fechaFin + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
