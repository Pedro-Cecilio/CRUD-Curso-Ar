package com.dbserver.crud_curso.domain.aluno;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dbserver.crud_curso.domain.aluno.dto.AtualizarDadosAlunoDto;
import com.dbserver.crud_curso.domain.aluno.dto.CriarAlunoDto;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;
import com.dbserver.crud_curso.domain.pessoa.Pessoa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Aluno extends Pessoa{
    private static final SimpleGrantedAuthority autoridade = new SimpleGrantedAuthority("ALUNO");

    @Column(nullable = false)
    private GrauEscolaridade grauEscolaridade;

    public Aluno(String email, String senha, String nome, String sobrenome,
            Long idade, GrauEscolaridade grauEscolaridade){
        super(email, senha, autoridade, nome, sobrenome, idade);
        this.grauEscolaridade = grauEscolaridade;
    }
    public Aluno(CriarAlunoDto dto){
        super(dto.email(), dto.senha(), autoridade, dto.nome(), dto.sobrenome(), dto.idade());
        this.grauEscolaridade = dto.grauEscolaridade();
    }
    
    public void atualizarDadosAluno(AtualizarDadosAlunoDto dto){
        super.atualizarDados(dto.senha(), dto.nome(), dto.sobrenome(), dto.idade());
    }


}
