package com.dbserver.crud_curso.domain.curso;

import com.dbserver.crud_curso.domain.curso.dto.AtualizarDadosCursoDto;
import com.dbserver.crud_curso.domain.curso.dto.CriarCursoDto;
import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Long duracaoMeses;

    @Column(nullable = false)
    private GrauEscolaridade grauEscolarMinimo;

    @Column(nullable = false)
    private GrauAcademico grauAcademicoMinimo;

    public Curso(CriarCursoDto dto) {
        setTitulo(dto.titulo());
        setDuracaoMeses(dto.duracaoMeses());
        setGrauEscolarMinimo(dto.grauEscolarMinimo());
        setGrauAcademicoMinimo(dto.grauAcademicoMinimo());
    }

    protected void atualizarDados(AtualizarDadosCursoDto dto) {
        setTitulo(dto.titulo() != null && !dto.titulo().isEmpty() ? dto.titulo() : this.titulo);
        setDuracaoMeses(dto.duracaoMeses() != null ? dto.duracaoMeses() : this.duracaoMeses);
        setGrauEscolarMinimo(dto.grauEscolarMinimo() != null && !dto.grauEscolarMinimo().isEmpty() ? dto.grauEscolarMinimo() : this.grauEscolarMinimo.toString());
        setGrauAcademicoMinimo(dto.grauAcademicoMinimo() != null && !dto.grauAcademicoMinimo().isEmpty() ? dto.grauAcademicoMinimo() : this.grauAcademicoMinimo.toString());
    }

    public void setTitulo(String titulo) {
        if (titulo == null)
            throw new IllegalArgumentException("Titulo deve ser informado");
        if (titulo.trim().length() < 3)
            throw new IllegalArgumentException("Titulo deve conter 3 caracteres no mínimo");
        this.titulo = titulo.trim();
    }

    public void setDuracaoMeses(Long duracaoMeses) {
        if (duracaoMeses == null)
            throw new IllegalArgumentException("Duração deve ser informada");
        if (duracaoMeses <= 0)
            throw new IllegalArgumentException("Duração deve ser maior do que zero");
        this.duracaoMeses = duracaoMeses;
    }

    public void setGrauEscolarMinimo(String grauEscolarMinimo) {
        try {
            this.grauEscolarMinimo = GrauEscolaridade.valueOf(grauEscolarMinimo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Grau de escolaridade mínimo inválido");
        }
    }

    public void setGrauAcademicoMinimo(String grauAcademicoMinimo) {
        try {
            this.grauAcademicoMinimo = GrauAcademico.valueOf(grauAcademicoMinimo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Grau acadêmico mínimo inválido");
        }
    }

}
