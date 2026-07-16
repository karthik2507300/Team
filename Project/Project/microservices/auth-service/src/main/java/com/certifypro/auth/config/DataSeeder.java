package com.certifypro.auth.config;

import com.certifypro.auth.common.Role;
import com.certifypro.auth.common.UserStatus;
import com.certifypro.auth.entity.User;
import com.certifypro.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds one demo user per role on startup (if missing).
 * Demo password for all seeded accounts: Password@123
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            seed(userRepository, passwordEncoder, "Programme Admin Demo", "admin@certifypro.com", Role.Admin);
            seed(userRepository, passwordEncoder, "Centre Admin Demo", "centreadmin@certifypro.com", Role.CentreAdmin);
            seed(userRepository, passwordEncoder, "Exam Controller Demo", "examcontroller@certifypro.com", Role.ExamController);
            seed(userRepository, passwordEncoder, "Evaluator Demo", "evaluator@certifypro.com", Role.Evaluator);
            seed(userRepository, passwordEncoder, "Certification Officer Demo", "certofficer@certifypro.com", Role.CertificationOfficer);
            seed(userRepository, passwordEncoder, "Candidate Demo", "candidate@certifypro.com", Role.Candidate);
        };
    }

    private void seed(UserRepository repo, PasswordEncoder encoder, String name, String email, Role role) {
        if (!repo.existsByEmail(email)) {
            repo.save(User.builder()
                    .name(name)
                    .email(email)
                    .phone("0000000000")
                    .passwordHash(encoder.encode("Password@123"))
                    .role(role)
                    .status(UserStatus.Active)
                    .build());
        }
    }
}
