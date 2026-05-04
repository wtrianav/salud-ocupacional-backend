package backend.salud_ocupacional.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_salud")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaToma;

    private Integer sistolica;  // mmHg
    private Integer diastolica; // mmHg
    private Integer pulsacion;  // lpm
    private Integer saturacion; // % SpO2

    private Double peso;     // kg
    private Double estatura; // metros

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Employee empleado;

    @Transient
    public Double getImc() {
        if (estatura != null && estatura > 0 && peso != null) {
            // Fórmula: Peso / Altura^2
            return Math.round((peso / (estatura * estatura)) * 100.0) / 100.0;
        }
        return 0.0;
    }

    @Transient
    public String getClasificacionImc() {
        double imc = getImc();
        if (imc < 18.5) return "Bajo Peso";
        if (imc < 24.9) return "Normal";
        if (imc < 29.9) return "Sobrepeso";
        return "Obesidad";
    }

    @Transient
    public String getEstadoTension() {
        if (sistolica >= 140 || diastolica >= 90) return "Hipertensión (Alerta)";
        if (sistolica >= 120 && sistolica < 140) return "Elevada";
        return "Normal";
    }
}
