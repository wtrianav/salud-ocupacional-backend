package backend.salud_ocupacional.repository;

import backend.salud_ocupacional.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByEmpleadoIdOrderByFechaTomaDesc(Long empleadoId);

    List<HealthRecord> findByFechaTomaBetween(LocalDateTime inicioDia, LocalDateTime finDia);

    List<HealthRecord> findByEmpleado_NumeroDocumentoOrderByFechaTomaAsc(String numeroDocumento);

    @Query("SELECT h FROM HealthRecord h JOIN FETCH h.empleado WHERE h.fechaToma = (SELECT MAX(h2.fechaToma) FROM HealthRecord h2 WHERE h2.empleado.id = h.empleado.id)")
    List<HealthRecord> findUltimosRegistrosPorEmpleado();
}