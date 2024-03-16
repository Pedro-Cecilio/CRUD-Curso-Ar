package com.dbserver.crud_curso.domain.professor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Professor extends Pessoa {
    private static final SimpleGrantedAuthority autoridade = new SimpleGrantedAuthority("PROFESSOR");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Professor(String email, String senha, String nome, String sobrenome,
            Long idade) {
        super(email, senha, autoridade, nome, sobrenome, idade);
    }

    @Column(nullable = false)
    private GrauAcademico grauAcademico;

}
