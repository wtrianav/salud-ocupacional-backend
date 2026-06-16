package backend.salud_ocupacional.service;

import backend.salud_ocupacional.model.HealthRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    public ByteArrayInputStream generarReporteDiarioExcel(List<HealthRecord> registros, LocalDate fecha) throws IOException {
        String[] columnas = {
                "ITEM", "FECHA", "NOMBRE Y APELLIDOS", "N° DOCUMENTO", "F. NACIMIENTO",
                "EDAD", "CARGO", "RH", "SISTOLICA", "DIASTOLICA", "PULSACIÓN",
                "SATURACIÓN", "TALLA", "PESO"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Tomas " + fecha.toString());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (HealthRecord record : registros) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx); // ITEM

                Cell fechaCell = row.createCell(1);
                fechaCell.setCellValue(record.getFechaToma().toLocalDate().format(formatter));

                row.createCell(2).setCellValue(record.getEmpleado().getNombres() + " " + record.getEmpleado().getApellidos());
                row.createCell(3).setCellValue(record.getEmpleado().getNumeroDocumento());
                row.createCell(4).setCellValue(record.getEmpleado().getFechaNacimiento().toString());
                row.createCell(6).setCellValue(record.getEmpleado().getCargo());

                row.createCell(8).setCellValue(record.getSistolica() != null ? record.getSistolica() : 0);
                row.createCell(9).setCellValue(record.getDiastolica() != null ? record.getDiastolica() : 0);
                row.createCell(10).setCellValue(record.getPulsacion() != null ? record.getPulsacion() : 0);
                row.createCell(11).setCellValue(record.getSaturacion() != null ? record.getSaturacion() + "%" : "");
                row.createCell(12).setCellValue(record.getEstatura() != null ? record.getEstatura() : 0.0);
                row.createCell(13).setCellValue(record.getPeso() != null ? record.getPeso() : 0.0);

                rowIdx++;
            }

            for (int col = 0; col < columnas.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarExpedienteEmpleadoExcel(List<HealthRecord> registros, String documentoEmpleado) throws IOException {
        String[] columnas = {
                "N° TOMA", "FECHA", "SISTOLICA", "DIASTOLICA", "PULSACIÓN",
                "SATURACIÓN", "TALLA", "PESO", "IMC", "CLASIFICACIÓN", "ESTADO TENSIÓN"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Expediente " + documentoEmpleado);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (HealthRecord record : registros) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx); // N° Toma
                row.createCell(1).setCellValue(record.getFechaToma().format(formatter));
                row.createCell(2).setCellValue(record.getSistolica() != null ? record.getSistolica() : 0);
                row.createCell(3).setCellValue(record.getDiastolica() != null ? record.getDiastolica() : 0);
                row.createCell(4).setCellValue(record.getPulsacion() != null ? record.getPulsacion() : 0);
                row.createCell(5).setCellValue(record.getSaturacion() != null ? record.getSaturacion() + "%" : "");
                row.createCell(6).setCellValue(record.getEstatura() != null ? record.getEstatura() : 0.0);
                row.createCell(7).setCellValue(record.getPeso() != null ? record.getPeso() : 0.0);
                row.createCell(8).setCellValue(record.getImc());
                row.createCell(9).setCellValue(record.getClasificacionImc());
                row.createCell(10).setCellValue(record.getEstadoTension());

                rowIdx++;
            }

            for (int col = 0; col < columnas.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReportePorRangoExcel(List<HealthRecord> registros, LocalDate inicio, LocalDate fin) throws IOException {
        String[] columnas = {
                "DOCUMENTO", "NOMBRE COMPLETO", "DEPENDENCIA", "CARGO", "FECHA TOMA",
                "SISTOLICA", "DIASTOLICA", "PULSACIÓN", "SATURACIÓN", "TALLA", "PESO",
                "IMC", "CLASIFICACIÓN", "ESTADO TENSIÓN"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte Rango");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columnas.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columnas[col]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowIdx = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (HealthRecord record : registros) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(record.getEmpleado().getNumeroDocumento());
                row.createCell(1).setCellValue(record.getEmpleado().getNombres() + " " + record.getEmpleado().getApellidos());
                row.createCell(2).setCellValue(record.getEmpleado().getDependencia() != null ? record.getEmpleado().getDependencia() : "N/A");
                row.createCell(3).setCellValue(record.getEmpleado().getCargo());

                row.createCell(4).setCellValue(record.getFechaToma().format(formatter));
                row.createCell(5).setCellValue(record.getSistolica() != null ? record.getSistolica() : 0);
                row.createCell(6).setCellValue(record.getDiastolica() != null ? record.getDiastolica() : 0);
                row.createCell(7).setCellValue(record.getPulsacion() != null ? record.getPulsacion() : 0);
                row.createCell(8).setCellValue(record.getSaturacion() != null ? record.getSaturacion() + "%" : "");
                row.createCell(9).setCellValue(record.getEstatura() != null ? record.getEstatura() : 0.0);
                row.createCell(10).setCellValue(record.getPeso() != null ? record.getPeso() : 0.0);
                row.createCell(11).setCellValue(record.getImc());
                row.createCell(12).setCellValue(record.getClasificacionImc());
                row.createCell(13).setCellValue(record.getEstadoTension());

                rowIdx++;
            }

            for (int col = 0; col < columnas.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}