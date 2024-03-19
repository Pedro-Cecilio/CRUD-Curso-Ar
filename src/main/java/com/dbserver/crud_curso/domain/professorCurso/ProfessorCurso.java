package com.dbserver.crud_curso.domain.professorCurso;

import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.professor.Professor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "professor_curso")
@Getter
public class ProfessorCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(nullable = false)
    private boolean criador;

    @Column(nullable = false)
    private boolean ativo = true;

    @Setter
    @Column(nullable = false)
    private boolean desativada = false;

    public ProfessorCurso(Professor professor, Curso curso, boolean criador) {
        this.curso = curso;
        this.criador = criador;
        this.professor = professor;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

}
