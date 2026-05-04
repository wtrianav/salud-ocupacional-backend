package backend.salud_ocupacional.service;

import backend.salud_ocupacional.model.Employee;
import backend.salud_ocupacional.model.HealthRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${google.gemini.api.key}")
    private String apiKey;

    // private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=";
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String generarDiagnostico(Employee empleado, HealthRecord ultimoRegistro) {
        if (ultimoRegistro == null) return "Se requiere al menos un registro de signos vitales para emitir un diagnóstico.";

        String prompt = String.format(
                "Actúa como un médico experto en salud ocupacional de la Alcaldía Municipal de Puerto Salgar (clima cálido). " +
                        "Analiza los siguientes datos del funcionario y genera un reporte directo y profesional:\n\n" +
                        "DATOS PERSONALES:\n" +
                        "- Nombre: %s %s\n" +
                        "- Edad: %d años\n" +
                        "- Género: %s\n" +
                        "- Cargo: %s (Dependencia: %s)\n\n" +
                        "ANTECEDENTES CLÍNICOS:\n" +
                        "- Enfermedades padecidas: %s\n" +
                        "- Alergias: %s\n" +
                        "- Cirugías/Operaciones: %s\n" +
                        "- Herencia Familiar: %s\n\n" +
                        "ÚLTIMOS SIGNOS VITALES:\n" +
                        "- Tensión Arterial: %d/%d mmHg\n" +
                        "- Frecuencia Cardíaca: %d lpm\n" +
                        "- SpO2: %d%%\n" +
                        "- Peso: %.1f kg (IMC: %.1f)\n\n" +
                        "Genera el reporte usando estrictamente estas 3 viñetas cortas (máximo 3 líneas por viñeta):\n" +
                        "1. Evaluación de Riesgo en Salud (basado en sus signos actuales y antecedentes).\n" +
                        "2. Riesgos Ocupacionales (riesgos asociados específicamente a su cargo en la Alcaldía).\n" +
                        "3. Recomendaciones Preventivas (hábitos, pausas activas o advertencias personalizadas a su perfil clínico).",
                empleado.getNombres(), empleado.getApellidos(), empleado.getEdadActual(),
                empleado.getGenero() != null ? empleado.getGenero().toString() : "No especificado",
                empleado.getCargo(), empleado.getDependencia(),
                empleado.getEnfermedadesPadecidas() != null && !empleado.getEnfermedadesPadecidas().isBlank() ? empleado.getEnfermedadesPadecidas() : "Ninguna",
                empleado.getAlergias() != null && !empleado.getAlergias().isBlank() ? empleado.getAlergias() : "Ninguna",
                empleado.getOperaciones() != null && !empleado.getOperaciones().isBlank() ? empleado.getOperaciones() : "Ninguna",
                empleado.getHerenciaFamiliar() != null && !empleado.getHerenciaFamiliar().isBlank() ? empleado.getHerenciaFamiliar() : "Ninguna",
                ultimoRegistro.getSistolica(), ultimoRegistro.getDiastolica(),
                ultimoRegistro.getPulsacion(), ultimoRegistro.getSaturacion(),
                ultimoRegistro.getPeso(), ultimoRegistro.getImc()
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            String urlConLlave = GEMINI_API_URL + apiKey;
            String response = restTemplate.postForObject(urlConLlave, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error de conexión con el Asistente IA. Verifique su conexión o llave API.";
        }
    }
}
