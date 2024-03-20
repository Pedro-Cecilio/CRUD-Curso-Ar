package com.dbserver.crud_curso.domain.curso.dto;

import com.dbserver.crud_curso.domain.curso.Curso;
import com.dbserver.crud_curso.domain.enums.GrauAcademico;
import com.dbserver.crud_curso.domain.enums.GrauEscolaridade;

public record CursoRespostaDto(Long id, String titulo, Long duracaoMeses, GrauEscolaridade grauEscolarMinimo, GrauAcademico grauAcademicoMinimo) {
    public CursoRespostaDto(Curso curso){
        this(curso.getId(), curso.getTitulo(), curso.getDuracaoMeses(), curso.getGrauEscolarMinimo(), curso.getGrauAcademicoMinimo());
    }
}
