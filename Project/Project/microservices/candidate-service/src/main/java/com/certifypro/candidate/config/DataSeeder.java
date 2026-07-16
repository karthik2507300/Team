package com.certifypro.candidate.config;

import com.certifypro.candidate.common.ProgramLevel;
import com.certifypro.candidate.common.ProgramStatus;
import com.certifypro.candidate.entity.CertificationProgram;
import com.certifypro.candidate.repository.CertificationProgramRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Seeds a couple of demo certification programs on startup (only if the table is empty).
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedPrograms(CertificationProgramRepository programRepository) {
        return args -> {
            if (programRepository.count() > 0) {
                return;
            }
            programRepository.save(CertificationProgram.builder()
                    .programName("Certified Cloud Foundation")
                    .body("CertifyPro")
                    .level(ProgramLevel.Foundation)
                    .eligibilityCriteria("Any graduate")
                    .examFee(new BigDecimal("2500.00"))
                    .validityYears(3)
                    .maxAttempts(3)
                    .status(ProgramStatus.Active)
                    .build());
            programRepository.save(CertificationProgram.builder()
                    .programName("Certified Data Professional")
                    .body("CertifyPro")
                    .level(ProgramLevel.Professional)
                    .eligibilityCriteria("2+ years experience in data engineering")
                    .examFee(new BigDecimal("6000.00"))
                    .validityYears(2)
                    .maxAttempts(2)
                    .status(ProgramStatus.Active)
                    .build());
        };
    }
}
