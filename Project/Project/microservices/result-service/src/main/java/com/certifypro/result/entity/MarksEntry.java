package com.certifypro.result.entity;

import com.certifypro.result.common.MarksStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A single evaluator's marks for a script (dual-marking allows up to two per script).
 * Intra-service relationship: @ManyToOne ScriptAllocation (join column script_id).
 * evaluatorId and verifiedById are auth-service userIds, kept as plain Long.
 */
@Entity
@Table(name = "marks_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarksEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marks_id")
    private Long marksId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "script_id", nullable = false)
    private ScriptAllocation script;

    @Column(name = "evaluator_id")
    private Long evaluatorId;

    @Column(name = "marks_awarded")
    private Integer marksAwarded;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "verified_by_id")
    private Long verifiedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MarksStatus status;
}
