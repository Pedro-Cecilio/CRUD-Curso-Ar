package com.dbserver.crud_curso.domain.alunoCurso.dto;

import jakarta.validation.constraints.NotNull;

public record AlunoCursoDadosEntradaDto(
        @NotNull(message = "id do curso deve ser informado")
        Long cursoId,
        
        @NotNull(message = "id do aluno deve ser informado")
        Long alunoId
        ) {

}
