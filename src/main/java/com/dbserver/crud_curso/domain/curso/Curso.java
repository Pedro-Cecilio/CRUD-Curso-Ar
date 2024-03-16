package com.dbserver.crud_curso.domain.curso;

import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Long duracaoMeses;

    @Column(nullable = false)
    private GrauEscolaridade nivelEscolarMinimo;

    @Column(nullable = false)
    private GrauAcademico grauAcademico;
}
