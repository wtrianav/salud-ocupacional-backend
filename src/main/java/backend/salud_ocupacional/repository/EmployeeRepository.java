package backend.salud_ocupacional.repository;

import backend.salud_ocupacional.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByNumeroDocumento(String numeroDocumento);
}