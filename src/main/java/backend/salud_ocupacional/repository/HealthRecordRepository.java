package backend.salud_ocupacional.repository;

import backend.salud_ocupacional.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    List<HealthRecord> findByEmpleadoIdOrderByFechaTomaDesc(Long empleadoId);
}