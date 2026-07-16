package com.certifypro.config;

import com.certifypro.model.User;
import com.certifypro.model.enums.Role;
import com.certifypro.model.enums.UserStatus;
import com.certifypro.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds one demo staff user per non-candidate role on startup (if missing).
 * Demo password for all seeded accounts: Password@123
 * (Candidates are created via self-registration, not seeded.)
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            seed(userRepository, passwordEncoder, "Programme Admin Demo",
                    "admin@certifypro.com", Role.Admin);
            seed(userRepository, passwordEncoder, "Centre Admin Demo",
                    "centreadmin@certifypro.com", Role.CentreAdmin);
            seed(userRepository, passwordEncoder, "Exam Controller Demo",
                    "examcontroller@certifypro.com", Role.ExamController);
            seed(userRepository, passwordEncoder, "Evaluator Demo",
                    "evaluator@certifypro.com", Role.Evaluator);
            seed(userRepository, passwordEncoder, "Certification Officer Demo",
                    "certofficer@certifypro.com", Role.CertificationOfficer);
            seed(userRepository, passwordEncoder, "Candidate Demo",
                    "candidate@certifypro.com", Role.Candidate);
        };
    }

    private void seed(UserRepository repo, PasswordEncoder encoder,
                      String name, String email, Role role) {
        if (!repo.existsByEmail(email)) {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPhone("0000000000");
            u.setPasswordHash(encoder.encode("Password@123"));
            u.setRole(role);
            u.setStatus(UserStatus.Active);
            repo.save(u);
        }
    }
}
