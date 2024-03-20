package com.dbserver.crud_curso.domain.alunoCurso;

import com.dbserver.crud_curso.domain.aluno.Aluno;
import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.enums.StatusMatricula;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aluno_curso")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlunoCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;
    
    @Column(nullable = false)
    private StatusMatricula statusMatricula;

    public AlunoCurso(Aluno aluno, Curso curso){
        this.aluno = aluno;
        this.curso = curso;
        this.statusMatricula = StatusMatricula.ATIVO;
    }

    public void setStatusMatricula(String statusMatricula){
        try {
            this.statusMatricula = StatusMatricula.valueOf(statusMatricula);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status de matrícula inválido");
        }
    }

    
}
