package com.projektas.itprojektas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "consultants")
@Table(name = "consultants")
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "credits", nullable = false)
    private double credits;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "roles_id", nullable = false)
    private Roles role;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "consultant_status_id", nullable = false)
    private ConsultantStatus consultantStatus;
}
