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
            Long idade, String grauEscolaridade){
        super(email, senha, autoridade, nome, sobrenome, idade);
        setGrauEscolaridade(grauEscolaridade);
    }
    public Aluno(CriarAlunoDto dto){
        super(dto.email(), dto.senha(), autoridade, dto.nome(), dto.sobrenome(), dto.idade());
        this.setGrauEscolaridade(dto.grauEscolaridade());
    }
    
    public void atualizarDadosAluno(AtualizarDadosAlunoDto dto){
        super.atualizarDados(dto.senha(), dto.nome(), dto.sobrenome(), dto.idade());
        this.setGrauEscolaridade(dto.grauEscolaridade() != null && !dto.grauEscolaridade().isEmpty() ? dto.grauEscolaridade() : this.grauEscolaridade.toString());
    }
    public void setGrauEscolaridade(String grauEscolaridade) {
        try {
            this.grauEscolaridade = GrauEscolaridade.valueOf(grauEscolaridade);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Grau de escolaridade inválido");
        }
    }
    

}
