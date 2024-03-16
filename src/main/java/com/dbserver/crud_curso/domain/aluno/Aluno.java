package com.dbserver.crud_curso.domain.aluno;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Aluno extends Pessoa{
    private static final SimpleGrantedAuthority autoridade = new SimpleGrantedAuthority("ALUNO");

    public Aluno(String email, String senha, String nome, String sobrenome,
            Long idade){
        super(email, senha, autoridade, nome, sobrenome, idade);
    }

    @Column(nullable = false)
    private GrauEscolaridade grauEscolaridade;

}
