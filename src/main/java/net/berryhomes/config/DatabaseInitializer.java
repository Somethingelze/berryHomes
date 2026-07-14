package net.berryhomes.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.model.entity.Admin;
import net.berryhomes.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Admin admin = Admin.builder()
                    .login("admin")
                    .role("ADMIN")
                    .passwordHash(passwordEncoder.encode("admin"))
                    .createdAt(ZonedDateTime.now())
                    .build();
            Admin createdAdmin = adminRepository.save(admin);

            log.info("Default admin account created successfully! {}", createdAdmin.toString());
        }
    }
}
