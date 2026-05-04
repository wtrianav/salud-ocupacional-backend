package backend.salud_ocupacional.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import backend.salud_ocupacional.model.enums.*;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroDocumento;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Enumerated(EnumType.STRING)
    private TipoSangre rh;

    private String cargo;
    private String dependencia;

    @Column(columnDefinition = "TEXT")
    private String enfermedadesPadecidas;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(columnDefinition = "TEXT")
    private String operaciones;

    @Column(columnDefinition = "TEXT")
    private String herenciaFamiliar;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HealthRecord> historicoSalud;

    @Transient
    public int getEdadActual() {
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }
}