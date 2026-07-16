package com.certifypro.result.entity;

import com.certifypro.result.common.ScriptStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A batch of answer scripts (identified by seat allocation + paper) assigned to an
 * evaluator. allocationId, evaluatorId and paperId reference rows owned by other
 * services (exam-service, auth-service, question-service) so they stay plain Long ids.
 */
@Entity
@Table(name = "script_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "script_id")
    private Long scriptId;

    @Column(name = "allocation_id")
    private Long allocationId;

    @Column(name = "evaluator_id")
    private Long evaluatorId;

    @Column(name = "paper_id")
    private Long paperId;

    @Column(name = "allocation_date")
    private LocalDate allocationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ScriptStatus status;
}
