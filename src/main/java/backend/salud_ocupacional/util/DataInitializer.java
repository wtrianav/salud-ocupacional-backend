package backend.salud_ocupacional.util;

import backend.salud_ocupacional.model.User;
import backend.salud_ocupacional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin_salud")
                    .password(passwordEncoder.encode("Salgar2026*"))
                    .roles(Set.of("ROLE_ADMIN"))
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Usuario administrador creado: admin_salud / Salgar2026*");
        }
    }
}