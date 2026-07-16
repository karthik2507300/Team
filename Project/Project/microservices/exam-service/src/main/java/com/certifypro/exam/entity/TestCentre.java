package com.certifypro.exam.entity;

import com.certifypro.exam.common.TestCentreStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_centre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCentre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "centre_id")
    private Long centreId;

    @Column(name = "centre_name")
    private String centreName;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "contact_person")
    private String contactPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TestCentreStatus status;
}
